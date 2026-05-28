plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:login:api"))
            implementation(project(":feature:invitation:api"))
            implementation(project(":feature:home:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.login.impl"
}
