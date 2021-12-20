plugins {
    base
    idea
    kotlin("jvm")
    id("com.google.cloud.tools.jib")
}

version = "0.0.1-SNAPSHOT"

val baseDockerImage: String by project
val containerRegistry: String by project

val excludedProjects = setOf("apps", "libs")
val restProjects = setOf("auth")
val grpcProjects = setOf("user", "organization", "review", "recommendation")


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
            if (path.startsWith(":apps") && (name in grpcProjects + restProjects)) {
                plugin("application")
                plugin("com.google.cloud.tools.jib")
            }

            if (path.startsWith(":libs")) {
                plugin("java-library")
            }
        }

        tasks {
            plugins.withId("com.google.cloud.tools.jib") {
                jib {
                    from {
                        if (project.hasProperty("baseDockerImage")) {
                            image = baseDockerImage
                        }
                    }
                    to {
                        if (project.hasProperty("containerRegistry")) {
                            image = "${containerRegistry}/${rootProject.name}-${project.name}:${project.version}"
                        }
                    }
                    container {
                        creationTime = "USE_CURRENT_TIMESTAMP"
                        labels.putAll(
                            mapOf(
                                "version" to "${project.version}",
                                "name" to project.name,
                                "group" to "${project.group}"
                            )
                        )
                        format = com.google.cloud.tools.jib.api.buildplan.ImageFormat.OCI
                    }
                }
            }
        }
    }
}

gradle.buildFinished {
    project.buildDir.deleteRecursively()
}
