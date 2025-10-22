pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "DungeonsXL"

// Main modules
include("api")
include("adapter")
include("bukkit_blockdata")
include("core")
include("dist")

// Addon modules
include("addon")
include("addon:core")
include("addon:dist")
