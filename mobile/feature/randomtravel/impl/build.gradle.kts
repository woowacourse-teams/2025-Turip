plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:randomtravel:api"))
            implementation(project(":feature:trip:api"))
            implementation(project(":feature:login:api"))
            implementation(project(":core:data"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.randomtravel.impl"
}
