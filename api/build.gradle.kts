plugins {
    `java-library`
    `maven-publish`
}

description = "DungeonsXL API"

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:${project.ext["paperVersion"]}")
}

tasks.jar {
    archiveBaseName.set("dungeonsxl-api")
}

// Configure javadoc generation
tasks.javadoc {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        links("https://jd.papermc.io/paper/1.21.8/")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "dungeonsxl-api"
        }
    }
}
