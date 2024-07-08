package ru.bk.oharass.freedomchat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import ru.bk.oharass.freedomchat.rewrite.CustomServerMetadata;

import java.util.Objects;
import java.util.function.Function;

@ChannelHandler.Sharable
public class FreedomHandler extends MessageToByteEncoder<Packet<?>> {
    private static final int STATUS_RESPONSE_PACKET_ID = 0x00;
    private final StreamCodec<ByteBuf, Packet<? super ClientGamePacketListener>> s2cPlayPacketCodec;
    private final boolean rewriteChat;
    private final boolean claimSecureChatEnforced;
    private final boolean noChatReports;

    public FreedomHandler(final boolean rewriteChat, final boolean claimSecureChatEnforced, final boolean noChatReports) {
        final RegistryAccess registryAccess = MinecraftServer.getServer().registryAccess();
        final Function<ByteBuf, RegistryFriendlyByteBuf> bufRegistryAccess = RegistryFriendlyByteBuf.decorator(registryAccess);
        this.s2cPlayPacketCodec = GameProtocols.CLIENTBOUND_TEMPLATE.bind(bufRegistryAccess).codec();
        this.rewriteChat = rewriteChat;
        this.claimSecureChatEnforced = claimSecureChatEnforced;
        this.noChatReports = noChatReports;
    }

    @Override
    public boolean acceptOutboundMessage(final Object msg) {
        return rewriteChat && msg instanceof ClientboundPlayerChatPacket
                || noChatReports && msg instanceof ClientboundStatusResponsePacket
                || claimSecureChatEnforced && msg instanceof ClientboundLoginPacket;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Packet msg, final ByteBuf out) {
        final FriendlyByteBuf fbb = new FriendlyByteBuf(out);

        if (msg instanceof final ClientboundPlayerChatPacket packet) {
            encode(ctx, packet, fbb);
        } else if (msg instanceof final ClientboundStatusResponsePacket packet) {
            encode(ctx, packet, fbb);
        } else if (msg instanceof final ClientboundLoginPacket packet) {
            encode(ctx, packet, fbb);
        }
    }

    private void encode(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final ClientboundPlayerChatPacket msg, final FriendlyByteBuf buf) {
        final Component content = Objects.requireNonNullElseGet(msg.unsignedContent(), () -> Component.literal(msg.body().content()));

        final ChatType.Bound chatType = msg.chatType();
        final Component decoratedContent = chatType.decorate(content);

        final ClientboundSystemChatPacket system = new ClientboundSystemChatPacket(decoratedContent, false);

        s2cPlayPacketCodec.encode(buf, system);
    }

    private void encode(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final ClientboundLoginPacket msg, final FriendlyByteBuf buf) {
        final ClientboundLoginPacket rewritten = new ClientboundLoginPacket(
                msg.playerId(),
                msg.hardcore(),
                msg.levels(),
                msg.maxPlayers(),
                msg.chunkRadius(),
                msg.simulationDistance(),
                msg.reducedDebugInfo(),
                msg.showDeathScreen(),
                msg.doLimitedCrafting(),
                msg.commonPlayerSpawnInfo(),
                true // Enforced secure chat
        );
        s2cPlayPacketCodec.encode(buf, rewritten);
    }

    private void encode(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final ClientboundStatusResponsePacket msg, final FriendlyByteBuf buf) {
        final ServerStatus status = msg.status();

        final CustomServerMetadata customStatus = new CustomServerMetadata(
                status.description(),
                status.players(),
                status.version(),
                status.favicon(),
                status.enforcesSecureChat(),
                true
        );

        buf.writeVarInt(STATUS_RESPONSE_PACKET_ID);
        buf.writeJsonWithCodec(CustomServerMetadata.CODEC, customStatus);
    }
}
