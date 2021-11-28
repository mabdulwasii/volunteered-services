plugins {
    base
    idea
    kotlin("jvm")
}

version = "0.0.1-SNAPSHOT"

val excludedProjects = setOf("apps", "libs")
val grpcProjects = setOf("user")

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    if (this.name !in excludedProjects) {
        version = rootProject.version

        if (path.startsWith(":apps")) {
            group = "org.volunteered.apps"
        }

        if (path.startsWith(":libs")) {
            group = "org.volunteered.libs"
        }

        apply {
            if (path.startsWith(":apps") && (name in grpcProjects)) {
                plugin("application")
            }

            if (path.startsWith(":libs")) {
                plugin("java-library")
            }
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-Xopt-in=kotlin.RequiresOptIn"
                )
            }
        }
    }
}

gradle.buildFinished {
    project.buildDir.deleteRecursively()
}
