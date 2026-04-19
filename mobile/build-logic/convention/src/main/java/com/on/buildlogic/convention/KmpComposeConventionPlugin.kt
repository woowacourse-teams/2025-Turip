package com.on.buildlogic.convention

import com.on.buildlogic.convention.extension.PluginIds
import com.on.buildlogic.convention.extension.library
import com.on.buildlogic.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * KMP(Compose Multiplatform) 모듈에서 **Compose UI 사용을 표준화**하기 위한 Convention Plugin.
 *
 * ## 역할
 * - Compose Multiplatform + Kotlin Compose 플러그인을 적용해, 멀티플랫폼(특히 `commonMain`)에서
 *   Compose UI를 바로 사용할 수 있는 기본 의존성을 일괄로 주입한다.
 * - 디버그 빌드에서만 필요한 UI Tooling 의존성을 추가해 프리뷰/인스펙션 경험을 보장한다.
 *
 * ## 적용되는 플러그인
 * - `org.jetbrains.compose` (Compose Multiplatform)
 * - `org.jetbrains.kotlin.plugin.compose` (Kotlin Compose)
 *
 * ## SourceSet 설정
 * ### commonMain
 * `commonMain`에서 Compose UI를 공유할 수 있도록 아래 의존성을 추가한다.
 * - Compose: runtime, foundation, material3, ui
 * - Compose Resources: `components.resources`
 * - Preview 지원: `components.uiToolingPreview`
 * - Koin Compose: `koin-compose` (공유 UI에서 DI 사용 목적)
 *
 * ## Debug 전용 의존성
 * - `debugImplementation`에 `compose.uiTooling`을 추가한다.
 *   (런타임에는 포함하지 않고, 디버그 프리뷰/툴링 환경에서만 사용)
 *
 * ## 사용 대상
 * - Compose Multiplatform 기반의 KMP 모듈(예: feature, core:ui, core:designsystem 등)
 * - `commonMain`에서 Compose UI를 직접 구성/공유하는 모듈
 *
 * ## 사용 예시
 * ```kotlin
 * plugins {
 *     id("dialog.convention.kmp.compose")
 * }
 * ```
 */
internal class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(PluginIds.COMPOSE_MULTIPLATFORM)
        plugins.apply(PluginIds.KOTLIN_COMPOSE)

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.named("commonMain") {
                dependencies {
                    implementation(libs.library("compose-runtime"))
                    implementation(libs.library("compose-foundation"))
                    implementation(libs.library("compose-ui"))
                    implementation(libs.library("compose-ui-tooling-preview"))
                    implementation(libs.library("compose-components-resources"))
                    implementation(libs.library("compose-material3"))
                    implementation(libs.library("koin-compose"))
                }
            }
        }

        dependencies {
            add("debugImplementation", "org.jetbrains.compose.ui:ui-tooling:1.10.0")
        }
    }
}
