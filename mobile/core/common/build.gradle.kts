plugins {
    id("turip.convention.kmp.library")
    id("turip.convention.kotlin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.core.model)
        }
    }
}

android {
    namespace = "com.on.turip.core.common"
}
