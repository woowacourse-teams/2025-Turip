plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:home:api"))
            implementation(project(":feature:search:api"))
            implementation(project(":feature:trip:api"))
            implementation(project(":feature:login:api"))
            implementation(project(":core:data"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.home.impl"
}
