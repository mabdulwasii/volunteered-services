version = "0.0.1-SNAPSHOT"

val excludedProjects = setOf("apps", "libs")

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
            if (path.startsWith(":libs")) {
                plugin("java-library")
            }
        }
    }
}