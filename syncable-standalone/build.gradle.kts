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
    implementation("org.fastmcmirror:yaml:1.3.0")
    implementation("org.yaml:snakeyaml:1.33")
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
    options.encoding = "UTF-8"
    options.release = 8
}

tasks.shadowJar {
    archiveClassifier = ""
}

tasks.getByName("build").finalizedBy(tasks.shadowJar)