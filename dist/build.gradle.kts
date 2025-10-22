plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

description = "DungeonsXL Distribution"

dependencies {
    // Internal dependencies - these will be shaded
    implementation(project(":adapter"))
    implementation(project(":api"))
    implementation(project(":bukkit_blockdata"))
    implementation(project(":core"))

    // External libraries from parent
    implementation("de.erethon.vignette:vignette-dist:1.0-SNAPSHOT-24")
}

tasks {
    shadowJar {
        archiveBaseName.set("DungeonsXL")
        archiveClassifier.set("")

        // Relocate vignette to avoid conflicts
        relocate("de.erethon.vignette", "de.erethon.dungeonsxl.util.vignette")

        // Include all dungeonsxl modules
        configurations = listOf(project.configurations.runtimeClasspath.get())

        // Minimize jar by removing unused classes
        minimize {
            exclude(dependency("de.erethon.vignette:vignette-dist:.*"))
        }
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
