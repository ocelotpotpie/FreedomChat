pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "FreedomChat"

sequenceOf("paper", "fabric").forEach {
    val capitalized = it.replaceFirstChar { c -> c.titlecase() }
    include("${rootProject.name}-$capitalized")
    project(":${rootProject.name}-$capitalized").projectDir = file(it)
}
