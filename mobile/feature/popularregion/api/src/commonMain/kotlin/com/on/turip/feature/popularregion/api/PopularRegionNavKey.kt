package com.on.turip.feature.popularregion.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * @param initialRegionCategoryName 진입하자마자 선택해 둘 지역 카테고리명. 홈 CTA 카드에서 특정 지역을 누르고 왔을 때 넘긴다.
 * null 이면 아무 지역도 고르지 않은 지도로 연다.
 */
@Serializable
data class PopularRegionNavKey(
    val initialRegionCategoryName: String? = null,
) : NavKey
