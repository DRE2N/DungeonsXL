plugins {
    `java-library`
}

description = "DungeonsXL Block Adapter Interface"

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:${project.ext["paperVersion"]}")

    // Internal dependencies
    api(project(":api"))
}

tasks.jar {
    archiveBaseName.set("dungeonsxl-adapter")
}
