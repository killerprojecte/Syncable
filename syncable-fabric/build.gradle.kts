plugins {
    id("java")
    id("fabric-loom") version "1.3-SNAPSHOT"
}

group = "dev.rgbmc"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.fastmcmirror.org/content/repositories/releases/")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.9:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.85.0+1.20.1")
    include(implementation(project(":syncable-client"))!!)
    include(implementation("org.fastmcmirror:yaml:1.3.0")!!)
    include(implementation("org.yaml:snakeyaml:1.33")!!)
    include(implementation("org.java-websocket:Java-WebSocket:1.5.4")!!)
}

tasks {

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }

    compileJava {
        options.release = 17
    }

}