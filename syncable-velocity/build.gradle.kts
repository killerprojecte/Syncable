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
    //implementation("org.fastmcmirror:yaml:1.3.0")
    //implementation("org.yaml:snakeyaml:1.33")
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    //compileOnly("org.spongepowered:configurate-yaml:3.7.3")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
}

tasks.compileJava {
    options.release = 11
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier = ""
    relocate("com.google.gson", "dev.rgbmc.syncable.libs.gson")
    relocate("org.java_websocket", "dev.rgbmc.syncable.libs.websocket")
    relocate("com.j256.ormlite", "dev.rgbmc.syncable.libs.ormlite")
    relocate("com.zaxxer.hikari", "dev.rgbmc.syncable.libs.hikari")
    relocate("org.fastmcmirror.yaml", "dev.rgbmc.syncable.libs.yaml")
    relocate("org.yaml.snakeyaml", "dev.rgbmc.syncable.libs.snakeyaml")
}

tasks.getByName("build").finalizedBy(tasks.shadowJar)