plugins {
    base
    idea
    jacoco
    id("org.sonarqube")
    kotlin("jvm")
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
    id("se.patrikerdes.use-latest-versions")
    id("com.dropbox.affectedmoduledetector")
    id("pl.allegro.tech.build.axion-release")
    id("com.google.cloud.tools.jib")
}

version = scmVersion.version

val jacocoQualityGate: String by project
val baseDockerImage: String by project
val containerRegistry: String by project

val ktlintVersion = libs.versions.ktlint.get()
val jacocoVersion = libs.versions.jacoco.get()

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

sonarqube {
    properties {
        property("sonar.java.codeCoveragePlugin", "jacoco")
        property("sonar.dynamicAnalysis", "reuseReports")
        property("sonar.exclusions", "**/*Generated.java")
    }
    tasks.sonarqube {
        subprojects.filter { it.name !in excludedProjects }.forEach {
            dependsOn(":${it.path}:check")
        }
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
            plugin("jacoco")
            plugin("org.sonarqube")
            plugin("com.diffplug.spotless")

            if (path.startsWith(":apps") && (name in grpcProjects + restProjects)) {
                plugin("application")
                plugin("com.google.cloud.tools.jib")
            }

            if (path.startsWith(":libs")) {
                plugin("java-library")
            }
        }

        jacoco {
            toolVersion = jacocoVersion
        }

        sonarqube {
            properties {
                property("sonar.junit.reportPaths", "$buildDir/test-results/test")
                property("sonar.java.binaries", "$buildDir/classes/java, $buildDir/classes/kotlin")
                property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/test/jacocoTestReport.xml")
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
            jacocoTestReport {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                }
            }

            jacocoTestCoverageVerification {
                violationRules {
                    rule { limit { minimum = jacocoQualityGate.toBigDecimal() } }
                    rule {
                        enabled = false
                        element = "CLASS"
                        includes = listOf("org.volunteered.libs.proto.*")
                    }
                }
            }

            test {
                useJUnitPlatform {
                    excludeTags("slow", "integration")
                }
                filter {
                    isFailOnNoMatchingTests = false
                }
                testLogging {
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                    showExceptions = true
                    showStandardStreams = true
                    events(
                        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
                    )
                }
                finalizedBy("jacocoTestReport")
            }

            register<Test>("integrationTest") {
                useJUnitPlatform {
                    includeTags("integration", "e2e")
                }
                filter {
                    isFailOnNoMatchingTests = false
                }
                testLogging {
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                    showExceptions = true
                    showStandardStreams = true
                    events(
                        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
                    )
                }
                shouldRunAfter(test)
                finalizedBy("jacocoTestReport")
            }

            check {
                dependsOn("jacocoTestCoverageVerification")
                dependsOn("jacocoTestReport")
                // dependsOn(integrationTest)
//                 dependsOn("spotlessCheck")
            }

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
        description = "Prints all affected subprojects due to code changes"
    }

    register("loadGitHubCredentials") {
        description = "Loads github credentials from environment. To be used by axion for pushing releases"

//        val token: String? = System.getenv("GITHUB_TOKEN")
        val token: String? = "GITHUB_TOKEN"
        if (token.isNullOrBlank()) {
            throw GradleException("GITHUB_TOKEN environment variable not found")
        } else {
            scmVersion.repository.customUsername = "Volunteered"
            scmVersion.repository.customPassword = token
        }
    }

    named("release") {
        dependsOn(":loadGitHubCredentials")
    }

    named("runAffectedUnitTests") {
        finalizedBy("affected")
    }
}

gradle.buildFinished {
    project.buildDir.deleteRecursively()
}

open class AffectedTask : DefaultTask() {
    @TaskAction
    fun printAffected() {
        println(
            """
            ################################
            #      Affected Modules        #
            ################################
            """.trimIndent()
        )
        project.subprojects.forEach {
            if (com.dropbox.affectedmoduledetector.AffectedModuleDetector.isProjectAffected(it)) {
                println(it.name)
            }
        }
    }
}
