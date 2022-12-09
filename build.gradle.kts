plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.3.7"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

group = "ru.bk.oharass.freedomchat"
version = "1.3.0"
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
}

bukkit {
    website = "https://github.com/sulu5890/FreedomChat"
    authors = listOf("Oharass", "sulu")
    main = "ru.bk.oharass.freedomchat.FreedomChat"
    apiVersion = "1.19"
}
