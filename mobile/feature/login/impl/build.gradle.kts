import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id("turip.convention.kotlin.feature.impl")
    alias(libs.plugins.buildkonfig)
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

        iosMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.darwin)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

buildkonfig {
    packageName = "com.on.turip.feature.login.impl"

    defaultConfigs {
        buildConfigField(
            STRING,
            "CLIENT_ID",
            "${gradleLocalProperties(rootDir, providers).getProperty("client_id") ?: ""}",
        )
        buildConfigField(
            STRING,
            "IOS_CLIENT_ID",
            "${gradleLocalProperties(rootDir, providers).getProperty("ios_client_id") ?: ""}",
        )
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
