plugins {
    id("turip.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.core.model)
            implementation(projects.core.network)
        }
    }
}

android {
    namespace = "com.on.turip.core.common"
}
