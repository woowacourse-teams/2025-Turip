package com.on.buildlogic.convention

import com.on.buildlogic.convention.extension.PluginIds
import com.on.buildlogic.convention.extension.library
import com.on.buildlogic.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * KMP(Kotlin Multiplatform) 기반 **Feature API 모듈 구성을 표준화**하기 위한 Convention Plugin.
 *
 * ## 역할
 * - Feature API 모듈에서 공통으로 필요한 KMP + Android Library 설정을 일괄 적용한다.
 * - 멀티플랫폼 공용 코드(`commonMain`)에서 사용할 핵심 의존성과
 *   Android Library로서의 기본 빌드 설정을 표준화한다.
 *
 * ## 적용되는 플러그인
 * - `org.jetbrains.kotlin.multiplatform`
 *   - 멀티플랫폼(KMP) 프로젝트 구성을 위한 핵심 플러그인
 * - `com.android.library`
 *   - Android 타겟을 Library 모듈로 구성하기 위한 플러그인
 * - `org.jetbrains.kotlin.plugin.serialization`
 *   - Kotlinx Serialization 사용을 위한 플러그인
 *
 * ## Kotlin Multiplatform 설정
 * - `configureDialogTargets()`
 *   - 프로젝트에서 정의한 KMP 타겟(Android, iOS 등)을 공통 규칙에 따라 설정
 * - `configureIosFrameworkForLibrary(project)`
 *   - iOS에서 사용할 Framework 형태로 빌드되도록 설정
 *
 * ### commonMain SourceSet
 * 멀티플랫폼 공용 코드에서 사용할 핵심 의존성을 추가한다.
 *
 * - `kotlinx-serialization-json`
 *   - 공통 계층에서 JSON 직렬화/역직렬화 처리
 * - `androidx-nav3-runtime`
 *   - Navigation 로직을 공통 계층(API 레벨)에서 참조하기 위한 런타임 의존성
 *
 * ## Android Library 설정
 * - `compileSdk`, `minSdk`를 Version Catalog를 통해 통일 관리
 * - `buildFeatures.buildConfig = true`
 *   - Feature API 모듈에서도 `BuildConfig` 사용 가능하도록 설정
 *
 * ### Java / Kotlin 컴파일 옵션
 * - Java 21을 기준으로 소스 및 타겟 호환성 설정
 *
 * ## 사용 대상
 * - KMP 기반 Feature API 모듈
 *   (예: feature:xxx:api)
 * - UI 없이 feature에 대한 navigation key를 포함하는 모듈
 *
 * ## 사용 예시
 * ```kotlin
 * plugins {
 *     id("dialog.convention.kmp.feature.api")
 * }
 * ```
 */
internal class KmpFeatureApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(PluginIds.KMP_LIBRARY_CONVENTION)
        plugins.apply(PluginIds.KOTLINX_SERIALIZATION)

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.named("commonMain") {
                dependencies {
                    implementation(libs.library("kotlinx-serialization-json"))
                    implementation(libs.library("jetbrains-navigation3-ui"))
                }
            }
        }
    }
}
