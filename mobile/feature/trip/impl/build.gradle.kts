plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:trip:api"))
            implementation(project(":feature:login:api"))
            implementation(project(":feature:turipdetail:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.trip.impl"
}
