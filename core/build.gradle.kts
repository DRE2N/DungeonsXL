plugins {
    `java-library`
}

description = "DungeonsXL Core Implementation"

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:${project.ext["paperVersion"]}")

    // Internal dependencies
    api(project(":adapter"))
    api(project(":api"))
    api(project(":bukkit_blockdata"))

    // Erethon libraries
    compileOnly("de.erethon.caliburn:caliburn-api:1.1")
    compileOnly("de.erethon.caliburn:calicomp:1.1")
    implementation("de.erethon.vignette:vignette-dist:1.0-SNAPSHOT-24")

    // Third-party plugin APIs
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("net.citizensnpcs:citizens:2.0.25-SNAPSHOT") {
        exclude(group = "net.citizensnpcs", module = "citizens-v1_8_R3")
        exclude(group = "net.citizensnpcs", module = "citizens-v1_10_R1")
        exclude(group = "net.citizensnpcs", module = "citizens-v1_11_R1")
        exclude(group = "net.citizensnpcs", module = "citizens-v1_12_R1")
        exclude(group = "net.citizensnpcs", module = "citizens-v1_13_R2")
        exclude(group = "net.citizensnpcs", module = "citizens-v1_14_R1")
        exclude(group = "net.citizensnpcs", module = "citizens-main")
    }
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.5")
    compileOnly("org.black_ixx:BossShop:2.7.5")
    compileOnly("com.griefcraft:Modern-LWC:2.1.2")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.alessiodp.parties:parties-api:3.1.10")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            "version" to project.version
        )
    }
}

tasks.jar {
    archiveBaseName.set("dungeonsxl-core")
}
