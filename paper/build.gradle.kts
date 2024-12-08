plugins {
    id("io.papermc.paperweight.userdev") version "1.7.4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
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
