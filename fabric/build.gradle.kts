plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
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
    minecraft(group = "com.mojang", name = "minecraft", version = "26.1.2")
    implementation(group = "net.fabricmc", name = "fabric-loader", version = "0.19.2")
    implementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.147.0+26.1.2")
    shade(implementation(group = "org.spongepowered", name = "configurate-yaml", version = "4.2.0"))
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(mapOf(
                "version" to project.version,
                "description" to project.description
            ))
        }
    }
    
    val shadowJarTask = named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        // Relocate configurate & its dependencies
        relocate("org.spongepowered.configurate", "ru.bk.oharass.freedomchat.lib.org.spongepowered.configurate")
        relocate("io.leangen.geantyref", "ru.bk.oharass.freedomchat.lib.io.leangen.geantyref")
        relocate("org.yaml.snakeyaml", "ru.bk.oharass.freedomchat.lib.org.yaml.snakeyaml")
        configurations = listOf(shade)
        archiveClassifier.set("")
    }
    
    named<Jar>("jar") {
        enabled = false
    }
    shadowJarTask.configure {
        archiveClassifier.set("")
    }
    assemble {
        dependsOn(shadowJarTask)
    }
}
