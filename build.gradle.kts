plugins {
    base
    idea
    kotlin("jvm")
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
    id("se.patrikerdes.use-latest-versions")
    id("com.dropbox.affectedmoduledetector")
    id("pl.allegro.tech.build.axion-release")
    id("com.google.cloud.tools.jib")
}

version = scmVersion.version

val baseDockerImage: String by project
val containerRegistry: String by project

val ktlintVersion = libs.versions.ktlint.get()

val excludedProjects = setOf("apps", "libs")
val restProjects = setOf("auth")
val grpcProjects = setOf("user", "organization", "review", "recommendation")

scmVersion {
    useHighestVersion = true

    tag(
        closureOf<pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig> {
            prefix = "v"
            versionSeparator = ""
        }
    )

    branchVersionIncrementer = mapOf(
        "feature/.*" to "incrementMinor",
        "hotfix/.*" to "incrementPatch",
        "release/.*" to "incrementPrerelease",
        "develop" to "incrementPatch",
        "main" to "incrementMinor"
    )
}

affectedModuleDetector {
    baseDir = "${project.rootDir}"
    pathsAffectingAllModules = setOf("gradle/libs.versions.toml")
    logFilename = "output.log"
    logFolder = "${rootProject.buildDir}/affectedModuleDetector"
    specifiedBranch = "develop"
    compareFrom = "SpecifiedBranchCommit"
}

//val token: String = System.getenv("GITHUB_TOKEN")
val shortRevision: String = scmVersion.scmPosition.shortRevision
val isSnapshot = version.toString().endsWith("-SNAPSHOT")
val isCI = System.getenv("CI").isNullOrBlank().not()
if (!project.hasProperty("release.quiet")) {
    println("Version: $version,  Branch: ${scmVersion.scmPosition.branch}, isCI: $isCI")
}

spotless {
    kotlin {
        ktlint(ktlintVersion)
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(ktlintVersion)
    }
}

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
            plugin("com.diffplug.spotless")

            if (path.startsWith(":apps") && (name in grpcProjects + restProjects)) {
                plugin("application")
                plugin("com.google.cloud.tools.jib")
            }

            if (path.startsWith(":libs")) {
                plugin("java-library")
            }
        }

        spotless {
            java {
                removeUnusedImports()
                trimTrailingWhitespace()
                endWithNewline()
                targetExclude("**/build/**")
            }
            kotlin {
                targetExclude("**/build/**")
                ktlint(ktlintVersion)
            }
            kotlinGradle {
                target("*.gradle.kts")
                ktlint(ktlintVersion)
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
                            image = "$containerRegistry/${rootProject.name}-${project.name}:${project.version}"
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

tasks {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    dependencyUpdates {
        rejectVersionIf {
            isNonStable(candidate.version)
        }

        outputDir = "$buildDir/dependencyUpdates"
        checkForGradleUpdate = true
        revision = "release"
        gradleReleaseChannel = "current"
    }

    register<AffectedTask>("affected") {
        group = "Affected Module Detector"
        description = "Prints all affected subprojects due to code changes"
    }

    register("loadGitHubToken") {
        description = "Loads github token from environment. To be used by axion for pushing releases"

        val token: String? = System.getenv("GITHUB_TOKEN")
        if (token.isNullOrBlank()) {
            println("Github token not found in environment. Please set GITHUB_TOKEN")
        } else {
            scmVersion.repository.customUsername = "Volunteered"
            scmVersion.repository.customPassword = token
        }
    }

    named("release") {
        dependsOn(":loadGitHubToken")
    }
}

gradle.buildFinished {
    project.buildDir.deleteRecursively()
}



open class AffectedTask : DefaultTask() {
    @TaskAction
    fun printAffected() {
        project.subprojects.forEach {
            println(
                "Is ${it.name} Affected? : " + com.dropbox.affectedmoduledetector.AffectedModuleDetector.isProjectAffected(
                    it
                )
            )
        }
    }
}

//open class LoadGithubTokenTask : DefaultTask() {
//    @Input
//    val token: String = System.getenv("GITHUB_TOKEN")
//
//    @TaskAction
//    fun loadGitHubToken() {
//        if (token.isBlank()) {
//            println("Github token not found in environment. Please set GITHUB_TOKEN")
//        } else {
//            scmVersion.repository.customUsername = "Volunteered"
//            scmVersion.repository.customPassword = token
//        }
//    }
//}
