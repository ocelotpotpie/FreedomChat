plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.5.3"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "ru.bk.oharass.freedomchat"
version = "1.3.1"
description = "Liberate your server from the chat-reporting bourgeoisie! Disable chat reporting with maximum compatibility."

dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
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
}

bukkit {
    website = "https://github.com/sulu5890/FreedomChat"
    authors = listOf("Oharass", "sulu")
    main = "ru.bk.oharass.freedomchat.FreedomChat"
    apiVersion = "1.19"
}
