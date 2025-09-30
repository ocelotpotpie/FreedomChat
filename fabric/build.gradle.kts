plugins {
    id("dev.architectury.loom") version "1.11-SNAPSHOT"
    id("com.gradleup.shadow") version "8.3.9"
}

val shade: Configuration by configurations.creating

repositories {
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots1"
        mavenContent { snapshotsOnly() }
    }
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = "1.21.9")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.21.9+build.1", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.17.2")
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.133.14+1.21.9")
    shade(implementation(group = "org.spongepowered", name = "configurate-yaml", version = "4.2.0"))
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "description" to project.description,
            )
        }
    }
    shadowJar {
        // Relocate configurate & its dependencies
        relocate("org.spongepowered.configurate", "ru.bk.oharass.freedomchat.lib.org.spongepowered.configurate")
        relocate("io.leangen.geantyref", "ru.bk.oharass.freedomchat.lib.io.leangen.geantyref")
        relocate("org.yaml.snakeyaml", "ru.bk.oharass.freedomchat.lib.org.yaml.snakeyaml")
        configurations = listOf(shade)
        archiveClassifier.set("dev")
    }
    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
    }
}
