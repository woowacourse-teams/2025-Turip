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
            implementation(project(":feature:turip:api"))
            implementation(project(":feature:mypage:api"))
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core)
        }
    }
}

android {
    namespace = "com.on.turip.feature.main"
}
