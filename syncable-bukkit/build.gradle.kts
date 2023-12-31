@file:Suppress("VulnerableLibrariesLocal")


plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.rgbmc"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.william278.net/releases")
    }
}

dependencies {
    compileOnly("folia:folia-api:1.20.1")
    compileOnly("net.md-5:bungeecord-chat:1.16-R0.4-deprecated+build.9")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.william278:husksync:2.2.8")
    implementation("de.tr7zw:item-nbt-api:2.12.2")
    implementation(project(":syncable-server"))
    implementation(project(":syncable-client"))
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.compileJava {
    options.release = 8
    options.encoding = "UTF-8"
}

tasks.processResources {
    val placeholders = mapOf(
        "version" to version
    )

    filesMatching("plugin.yml") {
        expand(placeholders)
    }
}

tasks.shadowJar {
    archiveClassifier = ""
    relocate("com.google.gson", "dev.rgbmc.syncable.libs.gson")
    relocate("org.java_websocket", "dev.rgbmc.syncable.libs.websocket")
    relocate("com.j256.ormlite", "dev.rgbmc.syncable.libs.ormlite")
    relocate("com.zaxxer.hikari", "dev.rgbmc.syncable.libs.hikari")
    relocate("de.tr7zw.changeme.nbtapi", "dev.rgbmc.syncable.libs.nbtapi")
}

tasks.getByName("build").finalizedBy(tasks.shadowJar)
