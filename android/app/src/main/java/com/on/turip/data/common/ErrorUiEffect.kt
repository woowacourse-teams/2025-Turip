package com.on.turip.data.common

/**
 * presentation/ui layer
 * 스낵바, 다이얼로그 같은 이벤트로 에러 상황을 사용자에게 알려주기 위한 UiEffect
 */
sealed interface ErrorUiEffect {
    data class ShowSnackbar(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : ErrorUiEffect
}
