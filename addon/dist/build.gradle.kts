plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

description = "DungeonsXXL Distribution"

dependencies {
    implementation(project(":addon:core"))
}

tasks {
    shadowJar {
        archiveBaseName.set("DungeonsXXL")
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
        dependsOn(shadowJar)
    }
}

artifacts {
    archives(tasks.shadowJar)
}
