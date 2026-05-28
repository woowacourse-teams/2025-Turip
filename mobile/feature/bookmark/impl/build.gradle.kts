plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:bookmark:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.bookmark.impl"
}
