import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.proto
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    kotlin("jvm")

    idea
    id("com.google.protobuf")
//    `java-test-fixtures`
}

version = "0.0.1-SNAPSHOT"

val grpcVersion = libs.versions.grpc.get()
val grpcKotlinVersion = libs.versions.grpcKotlin.get()
val protobufVersion = libs.versions.protobuf.get()
val pgvVersion = libs.versions.pgv.get()

dependencies {
    implementation(kotlin("stdlib"))

    // Grpc `io.grpc:grpc-all` has grpc-auth, grpc-alts, grpc-protobuf, grpc-xds ...
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub) // FIXME: remove me when grpc-kotlin support latest grpc-java version
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.kotlinx.coroutines.core)

    // Protobuf - If you want to use features like protobuf JsonFormat, `protobuf-java-util` instead of `protobuf-java`
    implementation(libs.protobuf.java)
    implementation(libs.protobuf.kotlin)

    // proto validate generator
    implementation(libs.pgv.java.stub)

    // Testing
//    testImplementation(testFixtures(project(":libs:test")))
    // grpc testing TODO: https://github.com/grpc/grpc-java/issues/5331
    // testImplementation(libs.grpc.test)
//    testFixturesImplementation(libs.protobuf.java)
}

// affectedTestConfiguration { jvmTestTask = "check" }

sourceSets {
    main {
        proto {
            // In addition to the default 'src/main/proto'
            srcDir("src/main/third_party_proto")
        }
    }
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        // Specify protoc to generate using kotlin protobuf plugin
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        // Specify protoc to generate using our grpc kotlin plugin
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
        }
        id("javapgv") {
            artifact = "io.envoyproxy.protoc-gen-validate:protoc-gen-validate:$pgvVersion"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Generate Java gRPC classes
                id("grpc")
                // Generate Kotlin gRPC classes
                id("grpckt")
                // Generate Validation classes
                // TODO enable this...
//                id("javapgv") {
//                    option("lang=java")
//                }
            }
            it.builtins {
                id("kotlin")
            }
            it.generateDescriptorSet = true
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        suppressWarnings = true
//        freeCompilerArgs = listOf(
//            "-Xopt-in=kotlin.RequiresOptIn"
//        )
    }
}
