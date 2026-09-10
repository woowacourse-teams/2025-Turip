package com.on.turip.feature.home.impl

sealed interface HomeUiEffect {
    data object NavigateToLogin : HomeUiEffect

    data object NavigateToRandomTravel : HomeUiEffect

    /**
     * 지역 목록이 아직 준비되지 않았을 때. 화면을 전환하지 않고 안내만 한다.
     * (슬롯이 돌다가 실패하는 그림을 만들지 않기 위한 사전 차단)
     */
    data object ShowRandomTravelUnavailable : HomeUiEffect
}
