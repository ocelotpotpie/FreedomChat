package ru.bk.oharass.freedomchat;

import net.kyori.adventure.key.Key;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static io.papermc.paper.network.ChannelInitializeListenerHolder.*;

public class FreedomChat extends JavaPlugin implements Listener {
    private static final Key listenerKey = Key.key("freedomchat", "listener");

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final FileConfiguration config = this.getConfig();

        final FreedomHandler handler = new FreedomHandler(
                config.getBoolean("rewrite-chat"),
                config.getBoolean("claim-secure-chat-enforced"),
                config.getBoolean("send-prevents-chat-reports-to-client"),
                this
        );

        addListener(listenerKey, channel -> channel.pipeline().addAfter("packet_handler", "freedom_handler", handler));
    }

    @Override
    public void onDisable() {
        if (hasListener(listenerKey)) removeListener(listenerKey);
    }
}
