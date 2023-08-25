plugins {
    id("java")
}

group = "dev.rgbmc"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.java-websocket:Java-WebSocket:1.5.4")
}