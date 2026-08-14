plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:data"))
            implementation(project(":feature:turip:api"))
            implementation(project(":feature:turipdetail:api"))
            implementation(project(":feature:login:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.turip.impl"
}
