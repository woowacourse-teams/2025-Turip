import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    kotlin("plugin.serialization") version "2.1.0"
}

android {
    namespace = "com.on.turip"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.on.turip"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = libs.versions.versionName.get()
        versionCode =
            libs.versions.versionCode
                .get()
                .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val baseUrl: String = gradleLocalProperties(rootDir, providers).getProperty("base_url")
        buildConfigField(
            "String",
            "BASE_URL",
            "\"$baseUrl\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    // aac viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    // retrofit2
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    // kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)
    // okhttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    // coil
    implementation(libs.coil)
    // WebView
    implementation(libs.androidx.webkit)
}
