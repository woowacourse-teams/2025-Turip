plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:search:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.search.impl"
}
