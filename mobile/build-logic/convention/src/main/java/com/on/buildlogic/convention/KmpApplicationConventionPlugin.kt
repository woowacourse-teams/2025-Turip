package com.on.buildlogic.convention

import com.android.build.api.dsl.ApplicationExtension
import com.on.buildlogic.convention.extension.PluginIds
import com.on.buildlogic.convention.extension.configureDialogTargets
import com.on.buildlogic.convention.extension.library
import com.on.buildlogic.convention.extension.libs
import com.on.buildlogic.convention.extension.version
import com.on.buildlogic.convention.extension.versionInt
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

/**
 * KMP 기반 **Android Application 모듈**을 위한 Convention Plugin.
 *
 * ## 역할
 * - Kotlin Multiplatform + Android Application 환경을 함께 구성한다.
 * - Compose Multiplatform 기반 앱(`composeApp`)에 필요한 공통/플랫폼별 설정을 일괄 적용한다.
 * - 앱 전용 iOS Framework 설정과 멀티플랫폼 타겟 구성을 담당한다.
 *
 * ## 적용되는 플러그인
 * - `org.jetbrains.kotlin.multiplatform`
 * - `com.android.application`
 * - `dialog.convention.kmp.compose`
 * - `io.sentry.kotlin.multiplatform.gradle` — Sentry KMP SDK 및 iOS Cocoa 의존성 자동 설치
 * - `com.codingfeline.buildkonfig` — 빌드 타입별 상수(DSN, IS_DEBUG 등) 주입
 *
 * ## Kotlin Multiplatform 설정
 * - `configureDialogTargets()`를 통해 Android / iOS 타겟을 구성한다.
 *
 * ### SourceSet 구성
 * - `commonMain`
 *   - KMP 공통 의존성 주입을 위한 `koin-core`
 *   - 멀티플랫폼 로깅을 위한 `napier`
 *   - 에러 모니터링을 위한 `sentry-kotlin-multiplatform` (Sentry Gradle Plugin이 자동 주입)
 *
 * - `androidMain`
 *   - Android 전용 Koin 확장 (`koin-android`)
 *   - Compose 환경에서의 DI 지원
 *     - `koin-compose`
 *     - `koin-compose-viewmodel`
 *     - `koin-compose-viewmodel-navigation`
 *
 * ## Android Application 설정
 * - compileSdk / minSdk / targetSdk 버전을 Version Catalog 기준으로 통일한다.
 * - Compose 및 BuildConfig 생성을 활성화한다.
 * - Java 21 컴파일 옵션을 적용한다.
 *
 * ## 사용 대상
 * - Compose Multiplatform 기반의 **KMP 앱 모듈**
 * - Android + iOS 앱을 동시에 제공하는 최상위 Application 모듈
 *
 * ## 사용 예시
 * ```kotlin
 * plugins {
 *     id("dialog.convention.kmp.application")
 * }
 * ```
 *
 * > 이 플러그인은 **라이브러리 모듈이 아닌 앱 모듈 전용**이며,
 * > `KmpLibraryConventionPlugin`과 명확히 역할이 분리된다.
 */
internal class KmpApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply {
            apply(PluginIds.KOTLIN_MULTIPLATFORM)
            apply(PluginIds.ANDROID_APPLICATION)
            apply("dialog.convention.kmp.compose")
            apply(PluginIds.BUILD_KONFIG)
            apply(PluginIds.SENTRY_KMP)
        }

        extensions.configure<KotlinMultiplatformExtension> {
            configureDialogTargets()

            targets.filterIsInstance<KotlinNativeTarget>().forEach { target ->
                target.binaries.framework {
                    baseName = "ComposeApp"
                    isStatic = true
                    binaryOption("bundleId", "com.on.dialog")
                    binaryOption("bundleVersion", libs.version("versionName"))
                    binaryOption("bundleShortVersionString", libs.version("versionName"))
                }
            }

            sourceSets.named("commonMain") {
                dependencies {
                    implementation(libs.library("koin-core"))
                    implementation(libs.library("napier"))
                }
            }

            sourceSets.named("androidMain") {
                dependencies {
                    implementation(libs.library("koin-android"))
                    implementation(libs.library("koin-compose"))
                    implementation(libs.library("koin-compose-viewmodel"))
                    implementation(libs.library("koin-compose-viewmodel-navigation"))
                }
            }
        }

        extensions.configure<ApplicationExtension> {
            compileSdk = libs.versionInt("compileSdk")
            defaultConfig {
                minSdk = libs.versionInt("minSdk")
                targetSdk = libs.versionInt("targetSdk")
            }
            buildFeatures {
                compose = true
                buildConfig = true
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }
        }
    }
}
