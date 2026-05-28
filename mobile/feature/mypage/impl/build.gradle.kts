plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:mypage:api"))
            implementation(project(":feature:bookmark:api"))
            implementation(project(":feature:trip:api"))
            implementation(project(":feature:login:api"))
}
    }
}

android {
    namespace = "com.on.turip.feature.mypage.impl"
}
