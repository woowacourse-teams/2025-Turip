package com.on.turip.feature.popularregion.impl

import com.on.turip.core.ui.UiEffect

sealed interface PopularRegionEffect : UiEffect {
    /**
     * 지역 브리핑으로 이동한다.
     *
     * 방문자 수를 함께 싣는 이유는 브리핑이 그 값을 다시 조회하지 않기 때문이다.
     * 시트에 보이던 숫자를 그대로 넘겨야 두 화면이 어긋나지 않는다.
     *
     * @param regionCategoryName 브리핑이 받는 카테고리명 (`강릉`). 시도명이 아니다.
     */
    data class NavigateToRegionBriefing(
        val regionCategoryName: String,
        val visitorCount: Long,
        val baseMonth: String?,
    ) : PopularRegionEffect

    data class NavigateToTripDetail(
        val contentId: Long,
    ) : PopularRegionEffect

    data object NavigateToLogin : PopularRegionEffect
}
