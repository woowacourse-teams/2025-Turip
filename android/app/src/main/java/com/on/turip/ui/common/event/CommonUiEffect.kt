package com.on.turip.ui.common.event

sealed interface CommonUiEffect {
    /**
     * 토큰 만료 시, 또는 그 외 로그인 화면으로 이동
     */
    data object NavigateToLogin : CommonUiEffect
}
