plugins {
    id("turip.convention.kmp.library")
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp.gradle.plugin)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.firebase.installations)
            implementation(libs.install.referrer)
            implementation(libs.kotlinx.coroutines.play.services)
        }
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:common"))
            implementation(project(":core:domain"))
            implementation(project(":core:data"))
            implementation(libs.napier)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.on.turip.core.local"
}
