package com.on.turip.feature.popularregion.impl

import com.on.turip.core.ui.UiEffect

sealed interface PopularRegionEffect : UiEffect {
    /** @param regionCategoryName 지역 결과 화면이 받는 카테고리명 (`강릉`). 시도명이 아니다. */
    data class NavigateToRegionResult(
        val regionCategoryName: String,
    ) : PopularRegionEffect

    data class NavigateToTripDetail(
        val contentId: Long,
    ) : PopularRegionEffect

    data object NavigateToLogin : PopularRegionEffect
}
