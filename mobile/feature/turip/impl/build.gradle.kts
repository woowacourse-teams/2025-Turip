plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:turip:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.turip.impl"
}
