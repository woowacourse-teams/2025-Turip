plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:invitation:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.invitation.impl"
}
