rootProject.name = "Syncable"
pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
include("syncable-server")
include("syncable-bukkit")
include("syncable-client")
include("syncable-fabric")
