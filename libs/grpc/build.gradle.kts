plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))

    // Grpc `io.grpc:grpc-all` has grpc-auth, grpc-alts, grpc-protobuf, grpc-xds ...
    runtimeOnly(libs.grpc.netty)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.bundles.kotlinx.coroutines)

    // Protobuf - If you want to use features like protobuf JsonFormat, `protobuf-java-util` instead of `protobuf-java`
//    implementation(libs.protobuf.java)

    implementation(libs.guava)
}
