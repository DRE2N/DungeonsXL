plugins {
    java
    `java-library`
}

group = "de.erethon.dungeonsxl"
version = "0.18-SNAPSHOT"
description = "Create custom dungeons and adventure maps with ease!"

// Configure all subprojects
subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "PaperMC"
        }
        maven("https://erethon.de/repo") {
            name = "Erethon"
        }
        maven("https://jitpack.io") {
            name = "JitPack"
        }
        maven("https://repo.citizensnpcs.co/") {
            name = "Citizens"
        }
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
            name = "PlaceholderAPI"
        }
        maven("https://repo.codemc.org/repository/maven-public/") {
            name = "CodeMC"
        }
        maven("https://repo.alessiodp.com/releases/") {
            name = "AlessioDP"
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    tasks.withType<ProcessResources> {
        filteringCharset = "UTF-8"
    }
}

// Common dependencies for all subprojects
allprojects {
    ext {
        set("paperVersion", "1.21.8-R0.1-SNAPSHOT")
    }
}
