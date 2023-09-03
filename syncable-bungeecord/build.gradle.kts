plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.rgbmc"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":syncable-server"))
    compileOnly("net.md-5:bungeecord-api:1.20-R0.2-SNAPSHOT")

}

tasks.compileJava {
    options.release = 8
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier = ""
    relocate("com.google.gson", "dev.rgbmc.syncable.libs.gson")
    relocate("org.java_websocket", "dev.rgbmc.syncable.libs.websocket")
    relocate("com.j256.ormlite", "dev.rgbmc.syncable.libs.ormlite")
    relocate("com.zaxxer.hikari", "dev.rgbmc.syncable.libs.hikari")
}

tasks.getByName("build").finalizedBy(tasks.shadowJar)