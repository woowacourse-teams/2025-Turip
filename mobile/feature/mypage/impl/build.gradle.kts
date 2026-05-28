plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:mypage:api"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.mypage.impl"
}
