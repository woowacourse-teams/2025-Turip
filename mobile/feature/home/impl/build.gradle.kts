plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:home:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.home.impl"
}
