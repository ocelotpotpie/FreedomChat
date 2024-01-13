plugins {
    id("dev.architectury.loom") version "1.3-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val shade: Configuration by configurations.creating

repositories {
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots1"
        mavenContent { snapshotsOnly() }
    }
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = "1.20.4")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.20.4+build.3", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.15.3")
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.92.0+1.20.4")
    modImplementation(include(group = "net.kyori", name = "adventure-platform-fabric", version = "5.11.0-SNAPSHOT"))
    shade(implementation(group = "org.spongepowered", name = "configurate-yaml", version = "4.1.2"))
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
        configurations = listOf(shade)
        archiveClassifier.set("dev")
    }
    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
    }
}
