plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    kotlin("kapt")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

version = "0.0.1-SNAPSHOT"

dependencies {

    kapt("org.hibernate:hibernate-jpamodelgen:5.4.30.Final")

    implementation(kotlin("stdlib"))

    implementation(project(":libs:core"))
    implementation(project(":libs:grpc"))
    implementation(project(":libs:proto"))

    implementation(libs.bundles.spring.grpc)

    implementation(libs.flyway.core)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.kotlin.reflect)
    implementation(libs.javax.validation)

    runtimeOnly(libs.h2)

    testImplementation(libs.spring.boot.starter.test)
}

tasks.test {
    useJUnitPlatform()
}
