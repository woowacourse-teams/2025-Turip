plugins {
    id("turip.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.compose.viewmodel)
        }
    }
}

android {
    namespace = "com.on.turip.core.ui"
}
