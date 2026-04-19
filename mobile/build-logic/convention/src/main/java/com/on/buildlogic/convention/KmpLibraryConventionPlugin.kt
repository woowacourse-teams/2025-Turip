package com.on.buildlogic.convention

import com.android.build.api.dsl.LibraryExtension
import com.on.buildlogic.convention.extension.PluginIds
import com.on.buildlogic.convention.extension.configureDialogTargets
import com.on.buildlogic.convention.extension.library
import com.on.buildlogic.convention.extension.libs
import com.on.buildlogic.convention.extension.versionInt
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * KMP 기반 **라이브러리 모듈**(Android Library + iOS Framework)을 위한 Convention Plugin.
 *
 * ## 역할
 * - Kotlin Multiplatform + Android Library 환경을 표준화한다.
 * - Android / iOS 타겟 구성을 통일하고, iOS에서 사용할 Framework 바이너리 설정을 적용한다.
 * - 공통 소스(`commonMain`)에서 사용할 기본 의존성과 직렬화 플러그인 적용을 일괄 처리한다.
 *
 * ## 적용되는 플러그인
 * - `org.jetbrains.kotlin.multiplatform`
 * - `com.android.library`
 * - `dialog.convention.kotlin.serialization`
 *
 * ## Kotlin Multiplatform 설정
 * - `configureDialogTargets()`로 Android / iOS 타겟을 구성한다.
 *
 * ### SourceSet 구성
 * - `commonMain`
 *   - 멀티플랫폼 로깅을 위한 `napier`
 *   - 공통 DI 구성을 위한 `koin-core`
 *
 * ## Android Library 설정
 * - compileSdk / minSdk 버전을 Version Catalog 기준으로 통일한다.
 * - BuildConfig 생성을 활성화한다. (`buildFeatures.buildConfig = true`)
 * - Java 21 컴파일 옵션을 적용한다.
 *
 * ## 사용 대상
 * - Compose 여부와 무관하게, KMP로 멀티플랫폼 코드를 제공하는 **공유 라이브러리 모듈**
 *   (예: core 모듈, shared 모듈, data/domain 성격의 멀티플랫폼 라이브러리 등)
 *
 * ## 사용 예시
 * ```kotlin
 * plugins {
 *     id("dialog.convention.kmp.library")
 * }
 * ```
 *
 * > 앱 전용 설정(예: ApplicationExtension, composeApp 구성)은 포함하지 않으며,
 * > `KmpApplicationConventionPlugin`과 역할이 분리된다.
 */
internal class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(PluginIds.KOTLIN_MULTIPLATFORM)
            plugins.apply(PluginIds.ANDROID_LIBRARY)

            extensions.configure<KotlinMultiplatformExtension> {
                configureDialogTargets()

                sourceSets.named("commonMain") {
                    dependencies {
                        implementation(libs.library("napier"))
                        implementation(libs.library("koin-core"))
                    }
                }
            }

            extensions.configure<LibraryExtension> {
                compileSdk = libs.versionInt("compileSdk")
                defaultConfig { minSdk = libs.versionInt("minSdk") }

                buildFeatures.buildConfig = true

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }
            }
        }
    }
}
