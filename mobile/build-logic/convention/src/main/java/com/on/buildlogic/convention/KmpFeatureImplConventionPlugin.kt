package com.on.buildlogic.convention

import com.on.buildlogic.convention.extension.PluginIds
import com.on.buildlogic.convention.extension.library
import com.on.buildlogic.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * KMP(Kotlin Multiplatform) 기반 **Feature Impl(UI 구현) 모듈 구성을 표준화**하기 위한 Convention Plugin.
 *
 * ## 역할
 * - Feature의 실제 구현 계층(`impl`)에서 필요한
 *   Compose Multiplatform UI 환경과 공통 Core 모듈 의존성을 일괄 적용한다.
 * - `commonMain`에서 Compose UI, 상태 관리, DI, 네비게이션을
 *   바로 사용할 수 있는 기본 환경을 제공한다.
 *
 * ## 적용되는 플러그인
 * - `org.jetbrains.kotlin.multiplatform`
 *   - 멀티플랫폼(KMP) 프로젝트 구성을 위한 핵심 플러그인
 * - `com.android.library`
 *   - Android 타겟을 Library 모듈로 구성
 * - `org.jetbrains.compose`
 *   - Compose Multiplatform UI 사용을 위한 플러그인
 * - `org.jetbrains.kotlin.plugin.compose`
 *   - Kotlin Compose 컴파일러 확장 플러그인
 * - `org.jetbrains.kotlin.plugin.serialization`
 *   - Kotlinx Serialization 사용을 위한 플러그인
 *
 * ## Kotlin Multiplatform 설정
 * - `configureDialogTargets()`
 *   - Android / iOS 등 KMP 타겟을 프로젝트 공통 규칙에 따라 설정
 * - `configureIosFrameworkForLibrary(project)`
 *   - iOS에서 Feature Impl 모듈을 Framework 형태로 제공
 *
 * ## SourceSet 설정
 * ### commonMain
 * Feature Impl 모듈의 **UI 및 비즈니스 흐름 구현**에 필요한 공통 의존성을 추가한다.
 *
 * #### Compose Multiplatform (공통 UI)
 * - `runtime`, `foundation`, `material3`, `ui`
 *   - Compose UI 구성에 필요한 핵심 라이브러리
 * - `components-resources`
 *   - Multiplatform 리소스 접근 지원
 * - `ui-tooling-preview`
 *   - 공통 코드에서 Preview 지원
 * - `koin-compose`
 *   - Compose UI에서 DI 사용
 *
 * #### Core 모듈 의존성
 * Feature 구현에 필요한 공통 Core 모듈을 참조한다.
 * - `core:ui`
 * - `core:network`
 * - `core:domain`
 * - `core:data`
 * - `core:local`
 * - `core:model`
 * - `core:navigation`
 * - `core:designsystem`
 * - `core:common`
 *
 * #### Feature 구현 공통 의존성
 * - `koin-compose-viewmodel`
 *   - ViewModel 기반 상태 관리
 * - `kotlinx-collections-immutable`
 *   - 불변 컬렉션 기반 상태 관리
 * - `androidx-nav3-runtime`
 *   - Feature 단위 네비게이션 구현
 * - `napier`
 *   - Multiplatform 로깅 라이브러리
 *
 * ## Debug 전용 의존성
 * - `debugImplementation`에 `compose.uiTooling` 추가
 *   - 런타임에는 포함하지 않고, 디버그 프리뷰/Inspector 환경에서만 사용
 *
 * ## Android Library 설정
 * - `compileSdk`, `minSdk`를 Version Catalog 기반으로 통일 관리
 * - `buildFeatures.buildConfig = true`
 *   - Feature Impl 모듈에서도 `BuildConfig` 사용 가능
 *
 * ### Java / Kotlin 컴파일 옵션
 * - Java 21 기준으로 소스 및 타겟 호환성 설정
 *
 * ## 사용 대상
 * - KMP 기반 Feature Impl(UI 구현) 모듈
 *   (예: feature:xxx:impl)
 * - Compose Multiplatform UI, ViewModel, 상태 관리, 네비게이션 로직을
 *   실제로 구현하는 모듈
 *
 * ## 사용 예시
 * ```kotlin
 * plugins {
 *     id("dialog.convention.kmp.feature.impl")
 * }
 * ```
 */

internal class KmpFeatureImplConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(PluginIds.KMP_LIBRARY_CONVENTION)
        plugins.apply(PluginIds.KMP_COMPOSE_CONVENTION)
        plugins.apply(PluginIds.KOTLINX_SERIALIZATION)

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.named("commonMain") {
                dependencies {
                    // core 공통
                    implementation(project(":core:ui"))
                    implementation(project(":core:domain"))
                    implementation(project(":core:model"))
                    implementation(project(":core:navigation"))
                    implementation(project(":core:designsystem"))
                    implementation(project(":core:common"))

                    // Feature 공통
                    implementation(libs.library("koin-compose-viewmodel"))
                    implementation(libs.library("kotlinx-collections-immutable"))
                    implementation(libs.library("jetbrains-navigation3-ui"))
                    implementation(libs.library("napier"))
                    implementation(libs.library("kotlinx-datetime"))
                }
            }
        }
    }
}
