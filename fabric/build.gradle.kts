plugins {
    alias(libs.plugins.gradle.fabric.loom)
    alias(libs.plugins.gradle.shadow)
}

val shade: Configuration by configurations.creating

repositories {
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots1"
        mavenContent { snapshotsOnly() }
    }
}

dependencies {
    minecraft(libs.minecraft.fabric.mojang)
    implementation(libs.minecraft.fabric.loader)
    implementation(libs.minecraft.fabric.api)
    implementation(libs.configurate.yaml)
    shade(libs.configurate.yaml)
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
