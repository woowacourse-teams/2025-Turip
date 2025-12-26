package com.on.turip.ui.common.event

import com.on.turip.data.common.UiError

sealed interface CommonUiEffect {
    /**
     * 토큰 만료 시, 또는 그 외 로그인 화면으로 이동
     */
    data object NavigateToLogin : CommonUiEffect

    /**
     * 서버 에러, 네트워크 에러 일 경우의 커스텀 에러 화면 표시
     */
    data class ShowErrorView(
        val error: UiError,
    ) : CommonUiEffect

    /**
     * 다이얼 로그, 스낵바 등으로 에러 내용 전달
     */
    data class NotifyError(
        val error: UiError,
    ) : CommonUiEffect
}
