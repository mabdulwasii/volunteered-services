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
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.jvm)

    runtimeOnly(libs.h2)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockk.test)
}

affectedTestConfiguration {
    jvmTestTask = "check"
}

application {
    mainClass.set("org.volunteered.apps.user.UserApplicationKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

jib {
    containerizingMode = "packaged"
    container {
        ports = listOf("9892")
        mainClass = application.mainClass.get()
    }
}
