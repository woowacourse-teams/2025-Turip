plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:article:api"))
            implementation(project(":feature:login:api"))
            implementation(project(":core:data"))

            implementation(libs.multiplatform.markdown.renderer)
            implementation(libs.multiplatform.markdown.renderer.m3)
            implementation(libs.multiplatform.markdown.renderer.coil3)
        }
    }
}

android {
    namespace = "com.on.turip.feature.article.impl"
}
