plugins {
}

group = "dev.rgbmc"
version = "1.0.0"

subprojects {
    repositories {
        maven {
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }
        maven {
            url = uri("https://oss.sonatype.org/content/groups/public/")
        }
    }

    apply(plugin = "java")

    dependencies {
        val compileOnly by configurations
        val implementation by configurations
        compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
        implementation("com.google.code.gson:gson:2.10.1")
    }
}