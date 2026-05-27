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
include(":feature:login:api")
include(":feature:invitation:api")
include(":feature:splash:api")
include(":feature:splash:impl")
include(":feature:main")
