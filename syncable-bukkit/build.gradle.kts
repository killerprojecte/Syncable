@file:Suppress("VulnerableLibrariesLocal")

import org.gradle.internal.impldep.org.yaml.snakeyaml.DumperOptions
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml


plugins {
    id("java")
}

group = "dev.rgbmc"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
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