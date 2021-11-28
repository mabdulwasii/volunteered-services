pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    enableFeaturePreview("VERSION_CATALOGS")
//    enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

    plugins {
        val kotlinVersion = "1.5.31"

        kotlin("jvm") version kotlinVersion

        kotlin("plugin.spring") version kotlinVersion apply false
        kotlin("plugin.jpa") version kotlinVersion apply false
        kotlin("plugin.allopen") version "1.4.32" apply false

        id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false

        id("org.springframework.boot") version "2.5.6" apply false
        id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false

        id("com.google.protobuf") version "0.8.17" apply false
    }
}

rootProject.name = "volunteered-services"

include(
    ":apps:auth",
    ":apps:user",

    ":libs:core",
    ":libs:grpc",
    ":libs:proto"
)
