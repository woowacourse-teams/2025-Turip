import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("turip.convention.kotlin.feature.impl")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:login:api"))
            implementation(project(":feature:invitation:api"))
            implementation(project(":feature:home:api"))
            implementation(project(":core:data"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)
        }
    }
}

android {
    namespace = "com.on.turip.feature.login.impl"

    defaultConfig {
        buildConfigField(
            "String",
            "CLIENT_ID",
            "\"${gradleLocalProperties(rootDir, providers).getProperty("client_id") ?: ""}\"",
        )
    }
}
