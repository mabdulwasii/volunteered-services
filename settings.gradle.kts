pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    buildscript {
        repositories {
            mavenCentral()
        }
        dependencies {
            classpath("org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r")
            classpath("com.dropbox.affectedmoduledetector:affectedmoduledetector:0.1.2")
        }
        configurations.classpath {
            resolutionStrategy {
                force("org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r")
            }
        }
    }

    enableFeaturePreview("VERSION_CATALOGS")
    enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

    plugins {
        val kotlinVersion = "1.6.0-RC"

        kotlin("jvm") version kotlinVersion

        kotlin("plugin.spring") version kotlinVersion apply false
        kotlin("plugin.jpa") version kotlinVersion apply false
        kotlin("plugin.allopen") version kotlinVersion apply false

        id("org.sonarqube") version "3.3"
        id("com.google.cloud.tools.jib") version "3.1.4"
        id("com.diffplug.spotless") version "5.17.0"
        id("com.github.ben-manes.versions") version "0.39.0"
        id("se.patrikerdes.use-latest-versions") version "0.2.17"
        id("pl.allegro.tech.build.axion-release") version "1.13.6"
        id("com.dropbox.affectedmoduledetector") version "0.1.2" apply false
        id("org.springframework.boot") version "2.5.6" apply false
        id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
        id("com.google.protobuf") version "0.8.17" apply false
    }
}

include(
    ":apps:auth",
    ":apps:user",
    ":apps:organization",
    ":apps:review",

    ":libs:core",
    ":libs:grpc",
    ":libs:proto"
)
