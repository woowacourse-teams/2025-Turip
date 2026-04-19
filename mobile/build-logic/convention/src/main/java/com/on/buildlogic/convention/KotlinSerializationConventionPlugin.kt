package com.on.buildlogic.convention

import com.on.buildlogic.convention.extension.PluginIds
import com.on.buildlogic.convention.extension.library
import com.on.buildlogic.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

/**
 * Kotlin Multiplatform 프로젝트에서 **kotlinx.serialization 사용을 표준화**하기 위한 Convention Plugin.
 *
 * ## 역할
 * - Kotlin Serialization 컴파일러 플러그인을 적용한다.
 * - `commonMain`에서 JSON 직렬화를 바로 사용할 수 있도록 기본 의존성을 주입한다.
 *
 * ## 적용되는 플러그인
 * - `org.jetbrains.kotlin.plugin.serialization`
 *
 * ## SourceSet 설정
 * ### commonMain
 * - `kotlinx.serialization.json`
 *   - 멀티플랫폼 환경에서 공통으로 사용할 JSON 직렬화 라이브러리
 *
 * ## 사용 대상
 * - Kotlin Multiplatform 기반 모듈
 * - Android / iOS / Desktop 등 플랫폼에 관계없이
 *   공통 비즈니스 로직에서 직렬화를 사용하는 모듈
 *
 * ## 사용 예시
 * ```kotlin
 * plugins {
 *     id("dialog.convention.kotlin.serialization")
 * }
 * ```
 *
 * > 이 플러그인은 Android / iOS 설정을 포함하지 않으며,
 * > 순수하게 **직렬화 플러그인 + 공통 의존성**만을 책임진다.
 */
internal class KotlinSerializationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(PluginIds.KOTLINX_SERIALIZATION)

        kotlinExtension.sourceSets.apply {
            named("commonMain") {
                dependencies {
                    implementation(libs.library("kotlinx.serialization.json"))
                }
            }
        }
    }
}
