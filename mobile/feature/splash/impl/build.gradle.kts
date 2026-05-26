plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:splash:api"))
            implementation(project(":feature:home:api"))
            implementation(project(":feature:login:api"))
            implementation(project(":feature:invitation:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.splash.impl"
}
