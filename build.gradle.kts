plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.5.3"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}

group = "ru.bk.oharass.freedomchat"
version = "1.4.2"
description = "Liberate your server from the chat-reporting bourgeoisie! Disable chat reporting with maximum compatibility."

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
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
        minecraftVersion("1.19.3")
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
