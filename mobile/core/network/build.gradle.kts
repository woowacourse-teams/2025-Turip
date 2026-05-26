plugins {
    id("turip.convention.kmp.library")
    id("turip.convention.kotlin.serialization")
    alias(libs.plugins.ksp.gradle.plugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:common"))
            implementation(project(":core:domain"))

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktorfit)
            implementation(libs.napier)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.ktorfit.ksp)
    add("kspAndroid", libs.ktorfit.ksp)
    add("kspIosArm64", libs.ktorfit.ksp)
    add("kspIosSimulatorArm64", libs.ktorfit.ksp)
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

tasks.matching { it.name != "kspCommonMainKotlinMetadata" && (it.name.startsWith("ksp") || it is org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>) }.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}

android {
    namespace = "com.on.turip.core.network"
}
