plugins {
    id("java-library")
}

subprojects {
    plugins.apply("java-library")

    group = "ru.bk.oharass.freedomchat"
    version = "1.7.7"
    description = "Liberate your server from the chat-reporting bourgeoisie! Disable chat signing server-side."

    tasks {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name()
        }
    }
}
