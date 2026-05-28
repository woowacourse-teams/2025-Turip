plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:login:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.login.impl"
}
