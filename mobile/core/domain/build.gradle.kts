plugins {
    id("turip.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:common"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.napier)
        }
    }
}

android {
    namespace = "com.on.turip.core.domain"
}
