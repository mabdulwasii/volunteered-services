plugins {
    kotlin("jvm")
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

    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.jdk14)
    implementation(libs.slf4j.simple)
}

application {
    mainClass.set("org.volunteered.apps.UserApplicationKt")
}
