package ru.bk.oharass.freedomchat.rewrite;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.status.ServerStatus;

import java.util.Optional;

public record CustomServerMetadata(Component description, Optional<ServerStatus.Players> players,
                                   Optional<ServerStatus.Version> version, Optional<ServerStatus.Favicon> favicon,
                                   boolean enforcesSecureChat, boolean preventsChatReports) {
    public static final Codec<CustomServerMetadata> CODEC = RecordCodecBuilder
            .create((instance) -> instance.group(
                            ComponentSerialization.CODEC.lenientOptionalFieldOf("description", CommonComponents.EMPTY)
                                    .forGetter(CustomServerMetadata::description),
                            ServerStatus.Players.CODEC.lenientOptionalFieldOf("players")
                                    .forGetter(CustomServerMetadata::players),
                            ServerStatus.Version.CODEC.lenientOptionalFieldOf("version")
                                    .forGetter(CustomServerMetadata::version),
                            ServerStatus.Favicon.CODEC.lenientOptionalFieldOf("favicon")
                                    .forGetter(CustomServerMetadata::favicon),
                            Codec.BOOL.lenientOptionalFieldOf("enforcesSecureChat", false)
                                    .forGetter(CustomServerMetadata::enforcesSecureChat),
                            Codec.BOOL.lenientOptionalFieldOf("preventsChatReports", false)
                                    .forGetter(CustomServerMetadata::preventsChatReports))
                    .apply(instance, CustomServerMetadata::new));

    public Component description() {
        return this.description;
    }

    public Optional<ServerStatus.Players> players() {
        return this.players;
    }

    public Optional<ServerStatus.Version> version() {
        return this.version;
    }

    public Optional<ServerStatus.Favicon> favicon() {
        return this.favicon;
    }

    public boolean enforcesSecureChat() {
        return this.enforcesSecureChat;
    }

    public boolean preventsChatReports() {
        return this.preventsChatReports;
    }
}
