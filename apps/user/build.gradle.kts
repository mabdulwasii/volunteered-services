plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":libs:core"))
    implementation(project(":libs:grpc"))
    implementation(project(":libs:proto"))

    implementation(libs.bundles.spring.grpc)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.javax.validation)

    testImplementation(libs.spring.boot.starter.test)
}

tasks.test {
    useJUnitPlatform()

    maxHeapSize = "1G"
}

