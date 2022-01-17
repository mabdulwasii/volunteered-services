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

dependencies {
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

affectedTestConfiguration {
    jvmTestTask = "check"
}

application {
    mainClass.set("org.volunteered.apps.review.ReviewApplicationKt")
}

jib {
    containerizingMode = "packaged"
    container {
        ports = listOf("9894")
        mainClass = application.mainClass.get()
    }
}
