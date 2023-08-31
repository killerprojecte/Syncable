rootProject.name = "Syncable"
pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://maven.neoforged.net/releases") {
            name = ""
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
include("syncable-server")
include("syncable-bukkit")
include("syncable-client")
include("syncable-fabric")
include("syncable-standalone")
include("syncable-forge")
include("syncable-velocity")
