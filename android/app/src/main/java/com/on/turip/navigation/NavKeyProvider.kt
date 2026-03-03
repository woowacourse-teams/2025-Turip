package com.on.turip.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

/**
 * Feature 단위로 **Navigation 구성 요소(NavKey + Screen)를 등록**하기 위한 Provider 인터페이스.
 *
 * ## 사용 대상
 * - KMP 기반 Feature 구현 모듈
 *   (예: feature:discussion:impl)
 * - Navigation 상태(`NavKey`)와 화면 구현을 함께 소유하는 Feature
 *
 * ## 사용 예시
 * ```kotlin
 * class ExampleNavKeyProvider : NavKeyProvider {
 *     override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
 *         subclass(
 *             ExampleNavKey::class,
 *             ExampleNavKey.serializer()
 *         )
 *     }
 *
 *     override fun EntryProviderScope<NavKey>.registerScreens(
 *         navigator: Navigator
 *     ) {
 *         exampleNavKeyScreen(navigator)
 *     }
 * }
 * ```
 */
interface NavKeyProvider {
    /**
     * Feature의 NavKey를 SerializersModule에 등록
     */
    fun PolymorphicModuleBuilder<NavKey>.registerNavKeys()

    /**
     * Feature의 화면을 EntryProvider에 등록
     */
    fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator)
}
