package ru.bk.oharass.freedomchat;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.Optional;

public class FreedomChat extends JavaPlugin implements Listener {
    private final Logger logger = this.getSLF4JLogger();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void on(final PlayerJoinEvent event) {
        final ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        final ChannelPipeline pipeline = player.connection.connection.channel.pipeline();

        pipeline.addAfter("packet_handler", "freedom_chat", new ChannelDuplexHandler() {
            public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {

                // rewrite all signed (or unsigned) player messages as system messages
                if (msg instanceof ClientboundPlayerChatPacket packet) {
                    final Component content = packet.message().unsignedContent().orElse(packet.message().signedContent().decorated());

                    final Optional<ChatType.Bound> ctbo = packet.chatType().resolve(player.level.registryAccess());
                    if (ctbo.isEmpty()) {
                        logger.warn("Processing packet with unknown ChatType {}", packet.chatType().chatType(), new Throwable());
                        return;
                    }
                    final Component decoratedContent = ctbo.orElseThrow().decorate(content);

                    super.write(ctx, new ClientboundSystemChatPacket(decoratedContent, false), promise);
                    return;
                }

                // remove unsigned content warning toast. all messages are now system.
                if (msg instanceof ClientboundServerDataPacket packet) {
                    super.write(ctx, new ClientboundServerDataPacket(packet.getMotd().orElse(null), packet.getIconBase64().orElse(null), packet.previewsChat(), true), promise);
                    return;
                }

                super.write(ctx, msg, promise);
            }
        });
    }
}