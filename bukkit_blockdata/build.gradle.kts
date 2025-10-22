plugins {
    `java-library`
}

description = "DungeonsXL Modern BlockData Adapter Implementation"

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:${project.ext["paperVersion"]}")

    // Internal dependencies
    api(project(":adapter"))
    api(project(":api"))
}

tasks.jar {
    archiveBaseName.set("dungeonsxl-bukkit-blockdata")
}
