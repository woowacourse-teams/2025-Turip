plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:article:api"))
            implementation(project(":feature:login:api"))
            implementation(project(":core:data"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.article.impl"
}
