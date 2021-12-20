pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    enableFeaturePreview("VERSION_CATALOGS")
    enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

    plugins {
        val kotlinVersion = "1.6.0-RC"

        kotlin("jvm") version kotlinVersion

        kotlin("plugin.spring") version kotlinVersion apply false
        kotlin("plugin.jpa") version kotlinVersion apply false
        kotlin("plugin.allopen") version kotlinVersion apply false

        id("org.springframework.boot") version "2.5.6" apply false
        id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false

        id("com.google.protobuf") version "0.8.17" apply false

        id("com.google.cloud.tools.jib") version "3.1.4"
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
