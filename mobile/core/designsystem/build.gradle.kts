plugins {
    id("turip.convention.kmp.library")
    id("turip.convention.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.multiplatform.markdown.renderer)
            implementation(libs.multiplatform.markdown.renderer.m3)
            implementation(libs.multiplatform.markdown.renderer.coil3)
        }
    }
}

compose.resources {
    packageOfResClass = "com.on.turip.core.designsystem.generated.resources"
    publicResClass = true
}

android {
    namespace = "com.on.turip.core.designsystem"
}