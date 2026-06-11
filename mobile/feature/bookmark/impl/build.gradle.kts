plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:data"))
            implementation(project(":feature:bookmark:api"))
            implementation(project(":feature:login:api"))
            implementation(project(":feature:trip:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.bookmark.impl"
}
