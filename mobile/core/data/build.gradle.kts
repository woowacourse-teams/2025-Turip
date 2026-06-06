plugins {
    id("turip.convention.kmp.library")
    id("turip.convention.kotlin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:common"))
            implementation(project(":core:domain"))
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "com.on.turip.core.data"
}
