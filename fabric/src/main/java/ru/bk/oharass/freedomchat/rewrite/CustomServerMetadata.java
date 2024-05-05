package ru.bk.oharass.freedomchat.rewrite;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Optional;

public record CustomServerMetadata(Text description, Optional<ServerMetadata.Players> players,
                                   Optional<ServerMetadata.Version> version, Optional<ServerMetadata.Favicon> favicon,
                                   boolean secureChatEnforced, boolean preventsChatReports) {
    public static final Codec<CustomServerMetadata> CODEC = RecordCodecBuilder
            .create((instance) -> instance.group(
                            TextCodecs.CODEC.lenientOptionalFieldOf("description", ScreenTexts.EMPTY)
                                    .forGetter(CustomServerMetadata::description),
                            ServerMetadata.Players.CODEC.lenientOptionalFieldOf("players")
                                    .forGetter(CustomServerMetadata::players),
                            ServerMetadata.Version.CODEC.lenientOptionalFieldOf("version")
                                    .forGetter(CustomServerMetadata::version),
                            ServerMetadata.Favicon.CODEC.lenientOptionalFieldOf("favicon")
                                    .forGetter(CustomServerMetadata::favicon),
                            Codec.BOOL.lenientOptionalFieldOf("enforcesSecureChat", false)
                                    .forGetter(CustomServerMetadata::secureChatEnforced),
                            Codec.BOOL.lenientOptionalFieldOf("preventsChatReports", true)
                                    .forGetter(CustomServerMetadata::preventsChatReports))
                    .apply(instance, CustomServerMetadata::new));

    public Text description() {
        return this.description;
    }

    public Optional<ServerMetadata.Players> players() {
        return this.players;
    }

    public Optional<ServerMetadata.Version> version() {
        return this.version;
    }

    public Optional<ServerMetadata.Favicon> favicon() {
        return this.favicon;
    }

    public boolean secureChatEnforced() {
        return this.secureChatEnforced;
    }

    public boolean preventsChatReports() {
        return this.preventsChatReports;
    }
}
