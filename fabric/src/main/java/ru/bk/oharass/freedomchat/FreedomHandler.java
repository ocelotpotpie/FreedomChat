package ru.bk.oharass.freedomchat;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.Optional;

@ChannelHandler.Sharable
public class FreedomHandler extends MessageToByteEncoder<Packet<?>> {

    private final boolean rewriteChat;
    private final boolean claimSecureChatEnforced;
    private final boolean noChatReports;
    private final FreedomChat freedom;

    public FreedomHandler(boolean rewriteChat, boolean claimSecureChatEnforced, boolean noChatReports, final FreedomChat freedom) {
        this.rewriteChat = rewriteChat;
        this.claimSecureChatEnforced = claimSecureChatEnforced;
        this.noChatReports = noChatReports;
        this.freedom = freedom;
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) {
        return rewriteChat && msg instanceof ChatMessageS2CPacket
                || noChatReports && msg instanceof QueryResponseS2CPacket
                || claimSecureChatEnforced && msg instanceof ServerMetadataS2CPacket;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) {
        final PacketByteBuf fbb = new PacketByteBuf(out);

        if (msg instanceof ChatMessageS2CPacket packet) {
            encode(ctx, packet, fbb);
        } else if (msg instanceof ServerMetadataS2CPacket packet) {
            encode(ctx, packet, fbb);
        } else if (msg instanceof QueryResponseS2CPacket packet) {
            encode(ctx, packet, fbb);
        }
    }

    private void encode(final ChannelHandlerContext ctx, final ChatMessageS2CPacket msg, final PacketByteBuf buf) {
        final Text content = Objects.requireNonNullElseGet(msg.unsignedContent(), () -> Text.literal(msg.body().content()));

        final Optional<MessageType.Parameters> ctbo = msg.serializedParameters().toParameters(freedom.getServer().getRegistryManager());
        if (ctbo.isEmpty()) {
            freedom.getLogger().warn("Processing packet with unknown ChatType " + msg.serializedParameters().typeId(), new Throwable());
            return;
        }
        final Text decoratedContent = ctbo.orElseThrow().applyChatDecoration(content);

        final GameMessageS2CPacket system = new GameMessageS2CPacket(decoratedContent, false);
        writeId(ctx, system, buf);
        system.write(buf);
    }

    private void encode(final ChannelHandlerContext ctx, final ServerMetadataS2CPacket msg, final PacketByteBuf buf) {
        writeId(ctx, msg, buf);
        buf.writeText(msg.getDescription());
        buf.writeOptional(msg.getFavicon(), PacketByteBuf::writeByteArray);
        buf.writeBoolean(true);
    }

    private void encode(final ChannelHandlerContext ctx, final QueryResponseS2CPacket msg, final PacketByteBuf buf) {
        final JsonObject status = ServerMetadata.CODEC
                .encodeStart(JsonOps.INSTANCE, msg.metadata())
                .get()
                .left()
                .orElseThrow(() -> new EncoderException("Failed to encode ServerStatus"))
                .getAsJsonObject();

        status.addProperty("preventsChatReports", true);

        writeId(ctx, msg, buf);
        buf.writeString(GsonComponentSerializer.gson().serializer().toJson(status));
    }

    private void writeId(final ChannelHandlerContext ctx, final Packet<?> packet, final PacketByteBuf buf) {
        buf.writeVarInt(ctx.channel().attr(ClientConnection.CLIENTBOUND_PROTOCOL_KEY).get().getId(packet));
    }
}
