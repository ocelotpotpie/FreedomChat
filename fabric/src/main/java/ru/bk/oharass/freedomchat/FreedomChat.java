package ru.bk.oharass.freedomchat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public class FreedomChat implements ModInitializer {
    public static final String MOD_ID = "freedomchat";
    private final Logger logger = LoggerFactory.getLogger(MOD_ID);
    private MinecraftServer server;
    private static FreedomHandler handler;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            if (!Boolean.getBoolean("im.evan.freedomchat.bypassprotocolcheck") && SharedConstants.getProtocolVersion() != 773) {
                logger.warn("This version of FreedomChat only supports protocol version 773 (1.21.9). Please use the appropriate version of FreedomChat for your server");
                logger.warn("If you know what you are doing, set the im.evan.freedomchat.bypassprotocolcheck system property to true to bypass this check");
                return;
            }
            final Path configPath = Path.of("config/FreedomChat/config.yml");
            final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(configPath)
                    .nodeStyle(NodeStyle.BLOCK)
                    .build();
            final CommentedConfigurationNode config;
            try {
                config = loader.load();
                final boolean rewriteChat = config.node("rewrite-chat").getBoolean(true);
                final boolean claimSecureChatEnforced = config.node("claim-secure-chat-enforced").getBoolean(false);
                final boolean noChatReports = config.node("send-prevents-chat-reports-to-client").getBoolean(false);
                final boolean bedrockOnly = config.node("rewrite-bedrock-only").getBoolean(false);
                loader.save(config);

                handler = new FreedomHandler(
                        this,
                        rewriteChat,
                        claimSecureChatEnforced,
                        noChatReports,
                        bedrockOnly
                );
            } catch (final ConfigurateException e) {
                logger.error("An error occurred while loading this configuration: " + e.getMessage());
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }
            }
        });
    }

    public Logger getLogger() {
        return logger;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public static FreedomHandler getHandler() {
        return handler;
    }
}
