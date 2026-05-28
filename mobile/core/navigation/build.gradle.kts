plugins {
    id("turip.convention.kmp.library")
    id("turip.convention.kmp.compose")
    id("turip.convention.kotlin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation3.runtime)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

android {
    namespace = "com.on.turip.core.navigation"
}
