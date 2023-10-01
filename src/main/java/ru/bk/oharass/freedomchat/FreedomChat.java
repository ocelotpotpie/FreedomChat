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
        if (!Boolean.getBoolean("im.evan.freedomchat.bypassprotocolcheck") && this.getServer().getUnsafe().getProtocolVersion() != 764) {
            getLogger().warning("This version of FreedomChat only supports protocol version 764 (1.20.2). Please use the appropriate version of FreedomChat for your server");
            getLogger().warning("If you know what you are doing, set the im.evan.freedomchat.bypassprotocolcheck system property to true to bypass this check");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

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
