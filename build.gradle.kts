plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "ru.bk.oharass.freedomchat"
version = "1.5.1"
description = "Liberate your server from the chat-reporting bourgeoisie! Disable chat reporting with maximum compatibility."

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    runServer {
        minecraftVersion("1.20.1")
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
