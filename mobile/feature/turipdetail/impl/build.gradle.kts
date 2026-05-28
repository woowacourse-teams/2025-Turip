plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:turipdetail:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.turipdetail.impl"
}
