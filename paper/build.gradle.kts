plugins {
    alias(libs.plugins.gradle.paperweight.userdev)
    alias(libs.plugins.gradle.run.paper)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle(libs.versions.minecraft.paperweight.bundle.get())
}

tasks {
    runServer {
        minecraftVersion(libs.versions.minecraft.mojang.version.get())
    }

    processResources {
        val props = mapOf(
            "version" to project.version,
            "description" to project.description
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
