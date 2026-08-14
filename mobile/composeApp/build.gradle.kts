import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    id("turip.convention.kmp.application")
    alias(libs.plugins.google.services)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:common"))
            implementation(project(":core:domain"))
            implementation(project(":core:network"))
            implementation(project(":core:data"))
            implementation(project(":core:local"))
            implementation(project(":core:navigation"))
            implementation(project(":feature:main"))
            implementation(project(":feature:splash:impl"))
            implementation(project(":feature:login:impl"))
            implementation(project(":feature:invitation:impl"))
            implementation(project(":feature:home:impl"))
            implementation(project(":feature:bookmark:impl"))
            implementation(project(":feature:mypage:impl"))
            implementation(project(":feature:search:impl"))
            implementation(project(":feature:turip:impl"))
            implementation(project(":feature:turipdetail:impl"))
            implementation(project(":feature:trip:impl"))
        }
    }
}

buildkonfig {
    packageName = "com.on.turip"

    defaultConfigs {
        buildConfigField(BOOLEAN, "IS_DEBUG", "true")
        buildConfigField(STRING, "SENTRY_DSN", "")
        buildConfigField(
            STRING,
            "BASE_URL",
            "${gradleLocalProperties(rootDir, providers).getProperty("debug_base_url")}",
        )
        buildConfigField(
            STRING,
            "CLIENT_ID",
            "${gradleLocalProperties(rootDir, providers).getProperty("client_id")}",
        )
        buildConfigField(
            STRING,
            "GOOGLE_MAPS_API_KEY",
            "${gradleLocalProperties(rootDir, providers).getProperty("google_maps_api_key") ?: ""}",
        )
    }

    defaultConfigs("release") {
        buildConfigField(BOOLEAN, "IS_DEBUG", "false")
        buildConfigField(
            STRING,
            "SENTRY_DSN",
            "${gradleLocalProperties(rootDir, providers).getProperty("sentry_dsn")}",
        )
        buildConfigField(
            STRING,
            "BASE_URL",
            "${gradleLocalProperties(rootDir, providers).getProperty("release_base_url")}",
        )
        buildConfigField(
            STRING,
            "CLIENT_ID",
            "${gradleLocalProperties(rootDir, providers).getProperty("client_id")}",
        )
        buildConfigField(
            STRING,
            "GOOGLE_MAPS_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("google_maps_api_key") ?: "",
        )
    }
}

android {
    namespace = "com.on.turip"
    val appLinkTuripInvitationHost =
        gradleLocalProperties(rootDir, providers).getProperty("app_link_turip_invitation_host")
            ?: "invite.turip.kro.kr"

    val androidKeystoreProperties =
        Properties().apply {
            val keystorePropertiesFile = rootDir.resolve("../android/keystore.properties").normalize()
            if (keystorePropertiesFile.isFile) {
                keystorePropertiesFile.inputStream().use(::load)
            }
        }

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
    signingConfigs {
        if (androidKeystoreProperties.isNotEmpty()) {
            getByName("debug") {
                storeFile = file(androidKeystoreProperties.getProperty("debug_store_file"))
                storePassword = androidKeystoreProperties.getProperty("debug_store_password")
                keyAlias = androidKeystoreProperties.getProperty("debug_key_alias")
                keyPassword = androidKeystoreProperties.getProperty("debug_key_password")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
            manifestPlaceholders +=
                mapOf(
                    "appName" to "@string/app_name_dev",
                    "appLinkTuripInvitationHost" to appLinkTuripInvitationHost,
                    "usesCleartextTraffic" to true,
                    "google_maps_api_key" to
                        (gradleLocalProperties(rootDir, providers).getProperty("google_maps_api_key") ?: ""),
                )
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders +=
                mapOf(
                    "appName" to "@string/app_name",
                    "appLinkTuripInvitationHost" to appLinkTuripInvitationHost,
                    "usesCleartextTraffic" to false,
                    "google_maps_api_key" to
                        (gradleLocalProperties(rootDir, providers).getProperty("google_maps_api_key") ?: ""),
                )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}
