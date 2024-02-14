package ru.bk.oharass.freedomchat;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

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
        return rewriteChat && msg instanceof ClientboundPlayerChatPacket
                || noChatReports && msg instanceof ClientboundStatusResponsePacket
                || claimSecureChatEnforced && msg instanceof ClientboundServerDataPacket;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) {
        final FriendlyByteBuf fbb = new FriendlyByteBuf(out);

        if (msg instanceof ClientboundPlayerChatPacket packet) {
            encode(ctx, packet, fbb);
        } else if (msg instanceof ClientboundServerDataPacket packet) {
            encode(ctx, packet, fbb);
        } else if (msg instanceof ClientboundStatusResponsePacket packet) {
            encode(ctx, packet, fbb);
        }
    }

    private void encode(final ChannelHandlerContext ctx, final ClientboundPlayerChatPacket msg, final FriendlyByteBuf buf) {
        final Component content = Objects.requireNonNullElseGet(msg.unsignedContent(), () -> Component.literal(msg.body().content()));

        final Optional<ChatType.Bound> ctbo = msg.chatType().resolve(MinecraftServer.getServer().registryAccess());
        if (ctbo.isEmpty()) {
            freedom.getLogger().log(Level.WARNING, "Processing packet with unknown ChatType " + msg.chatType().chatType(), new Throwable());
            return;
        }
        final Component decoratedContent = ctbo.orElseThrow().decorate(content);

        final ClientboundSystemChatPacket system = new ClientboundSystemChatPacket(decoratedContent, false);
        writeId(ctx, system, buf);
        system.write(buf);
    }

    private void encode(final ChannelHandlerContext ctx, final ClientboundServerDataPacket msg, final FriendlyByteBuf buf) {
        writeId(ctx, msg, buf);
        buf.writeComponent(msg.getMotd());
        buf.writeOptional(msg.getIconBytes(), FriendlyByteBuf::writeByteArray);
        buf.writeBoolean(true);
    }

    private void encode(final ChannelHandlerContext ctx, final ClientboundStatusResponsePacket msg, final FriendlyByteBuf buf) {
        final JsonObject status = ServerStatus.CODEC
                .encodeStart(JsonOps.INSTANCE, msg.status())
                .get()
                .left()
                .orElseThrow(() -> new EncoderException("Failed to encode ServerStatus"))
                .getAsJsonObject();

        status.addProperty("preventsChatReports", true);

        writeId(ctx, msg, buf);
        buf.writeUtf(GsonComponentSerializer.gson().serializer().toJson(status));
    }

    private void writeId(final ChannelHandlerContext ctx, final Packet<?> packet, final FriendlyByteBuf buf) {
        buf.writeVarInt(ctx.channel().attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).get().packetId(packet));
    }
}
