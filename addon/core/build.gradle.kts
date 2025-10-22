plugins {
    `java-library`
}

description = "DungeonsXXL Core - Enhanced addon implementation"

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:${project.ext["paperVersion"]}")

    // DungeonsXL dependency
    compileOnly(project(":dist"))
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            "version" to project.version
        )
    }
}

tasks.jar {
    archiveBaseName.set("dungeonsxl-addon-core")
}
