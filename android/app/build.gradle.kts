import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.serialization)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.compose.compiler)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktrofit)
}

android {
    namespace = "com.on.turip"
    compileSdk = 36

    val localProperties: Properties = gradleLocalProperties(rootDir, providers)
    val keystorePropertiesFile: File = rootProject.file("keystore.properties")
    val keystoreProperties: Properties =
        Properties().apply {
            if (!keystorePropertiesFile.exists()) error("keystore.properties 파일이 없습니다. android/keystore.properties를 생성해주세요.")
            keystorePropertiesFile.reader(Charsets.UTF_8).use { reader -> load(reader) }
        }

    fun requireLocalProperty(key: String): String = localProperties.getProperty(key) ?: error("local.properties에 $key 를 설정해주세요.")

    fun requireKeystoreProperty(key: String): String = keystoreProperties.getProperty(key) ?: error("keystore.properties에 $key 를 설정해주세요.")

    defaultConfig {
        applicationId = "com.on.turip"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionName = libs.versions.versionName.get()
        versionCode =
            libs.versions.versionCode
                .get()
                .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("debugTurip") {
            storeFile = file(requireKeystoreProperty("debug_store_file"))
            storePassword = requireKeystoreProperty("debug_store_password")
            keyAlias = requireKeystoreProperty("debug_key_alias")
            keyPassword = requireKeystoreProperty("debug_key_password")
        }

        create("releaseTurip") {
            storeFile = file(requireKeystoreProperty("release_store_file"))
            storePassword = requireKeystoreProperty("release_store_password")
            keyAlias = requireKeystoreProperty("release_key_alias")
            keyPassword = requireKeystoreProperty("release_key_password")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugTurip")

            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
            manifestPlaceholders["appName"] = "튜립.debug"

            buildConfigField("String", "BASE_URL", "\"${requireLocalProperty("debug_base_url")}\"")
            buildConfigField("String", "CLIENT_ID", "\"${requireLocalProperty("client_id")}\"")
        }

        release {
            signingConfig = signingConfigs.getByName("releaseTurip")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders["appName"] = "튜립"

            buildConfigField("String", "BASE_URL", "\"${requireLocalProperty("release_base_url")}\"")
            buildConfigField("String", "CLIENT_ID", "\"${requireLocalProperty("client_id")}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    lintChecks(project(":lint-rules"))

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
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    // kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)
    // ktorfit
    implementation(libs.ktorfit)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
    // ksp
    ksp(libs.androidx.room.compiler)
    // coil
    implementation(libs.coil)
    // WebView
    implementation(libs.androidx.webkit)
    // Timber
    implementation(libs.timber)
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.installations)
    // datastore
    implementation(libs.androidx.datastore.preferences)
    // Room
    implementation(libs.androidx.room.runtime)
    // viewpager2
    implementation(libs.androidx.viewpager2)
    // livedata
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    // ViewModel integration with Navigation3
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // compose
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.material.icons.extended)

    // maps
    implementation(libs.google.map)

    // app update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // credential
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // leak
    debugImplementation(libs.leakcanary.android)

    // immutable collection
    implementation(libs.kotlinx.collections.immutable)
}
