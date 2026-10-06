plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:regionbriefing:api"))
            // 연관 관광지 상세는 랜덤 여행이 이미 갖고 있다. 화면을 옮기지 않고 NavKey 만 참조한다.
            implementation(project(":feature:randomtravel:api"))
            implementation(project(":feature:trip:api"))
            implementation(project(":feature:login:api"))
            implementation(project(":core:data"))
        }
    }
}

android {
    namespace = "com.on.turip.feature.regionbriefing.impl"
}
