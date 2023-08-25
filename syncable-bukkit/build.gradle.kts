@file:Suppress("VulnerableLibrariesLocal")

import org.gradle.internal.impldep.org.yaml.snakeyaml.DumperOptions
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml


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
}

dependencies {
    implementation("de.tr7zw:item-nbt-api:2.11.3")
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
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.release = targetJavaVersion
    }
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