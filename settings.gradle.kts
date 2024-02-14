import org.gradle.configurationcache.extensions.capitalized

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
    }
}

rootProject.name = "FreedomChat"

sequenceOf(
    "paper",
    "fabric"
).forEach {
    include("${rootProject.name}-${it.capitalized()}")
    project(":${rootProject.name}-${it.capitalized()}").projectDir = file(it)
}
