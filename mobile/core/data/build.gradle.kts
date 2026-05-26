plugins {
    id("turip.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:common"))
            implementation(project(":core:domain"))
            implementation(project(":core:network"))
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "com.on.turip.core.data"
}
