plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:data"))
            implementation(project(":feature:turipdetail:api"))
            implementation(project(":feature:login:api"))
        }

        androidMain.dependencies {
            implementation(libs.google.map)
            implementation(libs.play.services.maps)
        }
    }
}

android {
    namespace = "com.on.turip.feature.turipdetail.impl"
}
