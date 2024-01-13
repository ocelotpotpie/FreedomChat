plugins {
    id("java-library")
}

subprojects {
    plugins.apply("java-library")

    group = "ru.bk.oharass.freedomchat"
    version = "1.5.2"
    description = "Liberate your server from the chat-reporting bourgeoisie! Disable chat signing server-side."

    tasks {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name()
        }
    }
}
