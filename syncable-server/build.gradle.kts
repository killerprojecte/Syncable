plugins {
    id("java")
}

group = "dev.rgbmc"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.j256.ormlite:ormlite-core:6.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.java-websocket:Java-WebSocket:1.5.4")
}