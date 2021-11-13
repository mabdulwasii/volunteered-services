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

        id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false

        id("org.springframework.boot") version "2.5.6" apply false
        id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false

        id("com.google.protobuf") version "0.8.17" apply false
    }
}

rootProject.name = "volunteered-server"
include("apps:auth")
findProject(":apps:auth")?.name = "auth"
include("libs:grpc")
findProject(":libs:grpc")?.name = "grpc"
include("libs:proto")
findProject(":libs:proto")?.name = "proto"
include("apps:user")
findProject(":apps:user")?.name = "user"
