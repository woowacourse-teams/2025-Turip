plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly("com.android.tools.lint:lint-api:31.9.1")

    testImplementation("com.android.tools.lint:lint:31.9.1")
    testImplementation("com.android.tools.lint:lint-tests:31.9.1")
    testImplementation(kotlin("test"))
}
