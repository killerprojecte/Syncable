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
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            url = uri("https://repo.fastmcmirror.org/content/repositories/releases/")
        }
    }

    apply(plugin = "java")

    dependencies {
        val implementation by configurations
        implementation("com.google.code.gson:gson:2.10.1")
    }
}