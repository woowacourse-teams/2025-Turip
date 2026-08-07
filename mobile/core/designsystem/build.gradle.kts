plugins {
    id("turip.convention.kmp.library")
    id("turip.convention.kmp.compose")
}

compose.resources {
    packageOfResClass = "com.on.turip.core.designsystem.generated.resources"
    publicResClass = true
}

android {
    namespace = "com.on.turip.core.designsystem"
}