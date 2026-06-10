plugins {
    id("turip.convention.kmp.library")
    id("turip.convention.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.material.icons.extended)
            implementation(project(":core:model"))
            implementation(project(":core:designsystem"))
        }
    }
}

android {
    namespace = "com.on.turip.core.ui"
}
