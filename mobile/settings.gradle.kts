pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "Turip"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":core:model")
include(":core:common")
include(":core:domain")
include(":core:network")
include(":core:data")
include(":core:local")
include(":core:designsystem")
include(":core:navigation")
include(":core:ui")

include(":feature:home:api")
include(":feature:home:impl")
include(":feature:login:api")
include(":feature:login:impl")
include(":feature:invitation:api")
include(":feature:invitation:impl")
include(":feature:splash:api")
include(":feature:splash:impl")
include(":feature:bookmark:api")
include(":feature:bookmark:impl")
include(":feature:mypage:api")
include(":feature:mypage:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:trip:api")
include(":feature:trip:impl")
include(":feature:turip:api")
include(":feature:turip:impl")
include(":feature:turipdetail:api")
include(":feature:turipdetail:impl")
include(":feature:main")
