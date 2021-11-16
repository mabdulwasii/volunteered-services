plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring") version "1.5.31"
    kotlin("plugin.jpa") version "1.5.31"
}

version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":libs:proto"))
    implementation(project(":libs:grpc"))

    // Grpc `io.grpc:grpc-all` has grpc-auth, grpc-alts, grpc-protobuf, grpc-xds ...
    runtimeOnly(libs.grpc.netty)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.services) // Optional. includes grpc-protobuf
    implementation(libs.grpc.xds) // Optional. includes grpc-services, grpc-auth,  grpc-alts
    implementation(libs.bundles.kotlinx.coroutines)

    // Protobuf - If you want to use features like protobuf JsonFormat, `protobuf-java-util` instead of `protobuf-java`
    implementation(libs.protobuf.java)
    implementation(libs.protobuf.kotlin)

    // Google
    implementation(libs.guava)

    // Kotlin Config
    implementation(libs.bundles.konf)

    // Resilience frameworks
    implementation(libs.sentinel.grpc.adapter)
    // implementation(libs.concurrency.limits.grpc)

//    implementation(libs.slf4j.api)
//    implementation(libs.kotlin.logging)
//    implementation(libs.slf4j.jdk14)
//    implementation(libs.slf4j.simple)

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("net.devh:grpc-spring-boot-starter:2.12.0.RELEASE")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("javax.validation:validation-api:2.0.1.Final")

    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}

application {
    mainClass.set("org.volunteered.apps.UserApplicationKt")
}
