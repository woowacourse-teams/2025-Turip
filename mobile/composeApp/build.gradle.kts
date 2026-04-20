import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id("turip.convention.kmp.application")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}

buildkonfig {
    packageName = "com.on.turip"

    defaultConfigs {
        buildConfigField(BOOLEAN, "IS_DEBUG", "true")
        buildConfigField(STRING, "SENTRY_DSN", "")
    }

    defaultConfigs("release") {
        buildConfigField(BOOLEAN, "IS_DEBUG", "false")
        buildConfigField(
            STRING,
            "SENTRY_DSN",
            "${gradleLocalProperties(rootDir, providers).getProperty("sentry_dsn")}",
        )
    }
}

android {
    namespace = "com.on.turip"

    defaultConfig {
        applicationId = "com.on.turip"
        versionCode =
            libs.versions.versionCode
                .get()
                .toInt()
        versionName = libs.versions.versionName.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".dev"
            versionNameSuffix = ".dev"
            manifestPlaceholders +=
                mapOf(
                    "appName" to "@string/app_name_dev",
                )
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders +=
                mapOf(
                    "appName" to "@string/app_name",
                )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}
