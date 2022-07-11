package ru.bk.oharass.freedomchat;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ChatTypeDecoration;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FreedomChat extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler()
    public void on(final PlayerJoinEvent event) {
        final ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        final ChannelPipeline pipeline = player.connection.connection.channel.pipeline();

        pipeline.addAfter("packet_handler", "freedom_chat", new ChannelDuplexHandler() {
            public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
                if (msg instanceof ClientboundPlayerChatPacket packet) {
                    final Component content = packet.unsignedContent().orElse(packet.signedContent());

                    final Registry<ChatType> registry = player.level.registryAccess().registryOrThrow(Registry.CHAT_TYPE_REGISTRY);
                    final ChatTypeDecoration decoration = registry.byId(packet.typeId()).chat();

                    final Component decoratedContent = decoration.decorate(content, packet.sender());
                    super.write(ctx, new ClientboundSystemChatPacket(decoratedContent, false), promise);
                    return;
                }

                super.write(ctx, msg, promise);
            }
        });
    }
}