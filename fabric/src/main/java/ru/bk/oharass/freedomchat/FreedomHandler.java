package ru.bk.oharass.freedomchat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import ru.bk.oharass.freedomchat.rewrite.CustomServerMetadata;

import java.util.Objects;
import java.util.function.Function;

@ChannelHandler.Sharable
public class FreedomHandler extends MessageToByteEncoder<Packet<?>> {
    private static final int STATUS_RESPONSE_PACKET_ID = 0x00;
    private final PacketCodec<ByteBuf, Packet<? super ClientPlayPacketListener>> s2cPlayPacketCodec;
    private final boolean rewriteChat;
    private final boolean claimSecureChatEnforced;
    private final boolean noChatReports;

    public FreedomHandler(final FreedomChat freedom, final boolean rewriteChat, final boolean claimSecureChatEnforced, final boolean noChatReports) {
        final DynamicRegistryManager registryAccess = freedom.getServer().getRegistryManager();
        final Function<ByteBuf, RegistryByteBuf> bufRegistryAccess = RegistryByteBuf.makeFactory(registryAccess);
        this.s2cPlayPacketCodec = PlayStateFactories.S2C.bind(bufRegistryAccess).codec();
        this.rewriteChat = rewriteChat;
        this.claimSecureChatEnforced = claimSecureChatEnforced;
        this.noChatReports = noChatReports;
    }

    @Override
    public boolean acceptOutboundMessage(final Object msg) {
        return rewriteChat && msg instanceof ChatMessageS2CPacket
                || noChatReports && msg instanceof QueryResponseS2CPacket
                || claimSecureChatEnforced && msg instanceof GameJoinS2CPacket;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Packet msg, final ByteBuf out) {
        final PacketByteBuf fbb = new PacketByteBuf(out);

        if (msg instanceof final ChatMessageS2CPacket packet) {
            encode(ctx, packet, fbb);
        } else if (msg instanceof final QueryResponseS2CPacket packet) {
            encode(ctx, packet, fbb);
        } else if (msg instanceof final GameJoinS2CPacket packet) {
            encode(ctx, packet, fbb);
        }
    }

    private void encode(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final ChatMessageS2CPacket msg, final PacketByteBuf buf) {
        final Text content = Objects.requireNonNullElseGet(msg.unsignedContent(), () -> Text.literal(msg.body().content()));

        final MessageType.Parameters chatType = msg.serializedParameters();
        final Text decoratedContent = chatType.applyChatDecoration(content);

        final GameMessageS2CPacket system = new GameMessageS2CPacket(decoratedContent, false);

        s2cPlayPacketCodec.encode(buf, system);
    }

    private void encode(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final GameJoinS2CPacket msg, final PacketByteBuf buf) {
        final GameJoinS2CPacket rewritten = new GameJoinS2CPacket(
                msg.playerEntityId(),
                msg.hardcore(),
                msg.dimensionIds(),
                msg.maxPlayers(),
                msg.viewDistance(),
                msg.simulationDistance(),
                msg.reducedDebugInfo(),
                msg.showDeathScreen(),
                msg.doLimitedCrafting(),
                msg.commonPlayerSpawnInfo(),
                true // Enforced secure chat
        );
        s2cPlayPacketCodec.encode(buf, rewritten);
    }

    private void encode(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final QueryResponseS2CPacket msg, final PacketByteBuf buf) {
        final ServerMetadata status = msg.metadata();

        final CustomServerMetadata customStatus = new CustomServerMetadata(
                status.description(),
                status.players(),
                status.version(),
                status.favicon(),
                status.secureChatEnforced(),
                true
        );

        buf.writeVarInt(STATUS_RESPONSE_PACKET_ID);
        buf.encodeAsJson(CustomServerMetadata.CODEC, customStatus);
    }
}
