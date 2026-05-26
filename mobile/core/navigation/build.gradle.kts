plugins {
    id("turip.convention.kmp.library")
    id("turip.convention.kotlin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation3.runtime)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

android {
    namespace = "com.on.turip.core.navigation"
}
