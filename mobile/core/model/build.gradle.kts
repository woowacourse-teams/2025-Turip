plugins {
    id("turip.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.on.turip.core.model"
}
