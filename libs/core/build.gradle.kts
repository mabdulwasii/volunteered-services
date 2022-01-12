plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
}

affectedTestConfiguration {
    jvmTestTask = "check"
}
