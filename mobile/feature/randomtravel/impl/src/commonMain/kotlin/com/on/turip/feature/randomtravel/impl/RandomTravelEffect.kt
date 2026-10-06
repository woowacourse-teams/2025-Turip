package com.on.turip.feature.randomtravel.impl

import com.on.turip.core.ui.UiEffect

sealed interface RandomTravelEffect : UiEffect {
    /** 여행지가 확정되는 순간의 가벼운 진동 */
    data object PerformDestinationHaptic : RandomTravelEffect

    data class NavigateToTripDetail(
        val contentId: Long,
    ) : RandomTravelEffect

    /** 장소를 담은 직후에는 영상이 아니라 방금 만든 튜립을 보여준다. */
    data class NavigateToTuripDetail(
        val turipId: Long,
    ) : RandomTravelEffect

    data object NavigateToLogin : RandomTravelEffect

    /**
     * 튜립 생성 결과. 장소 담기는 1건씩 요청하기 때문에 일부만 담길 수 있어
     * 실제로 담긴 수와 담으려던 수를 함께 전달한다.
     */
    data class ShowTuripCreated(
        val turipName: String,
        val savedCount: Int,
        val requestedCount: Int,
    ) : RandomTravelEffect

    data object ShowTuripCreateFailed : RandomTravelEffect
}
