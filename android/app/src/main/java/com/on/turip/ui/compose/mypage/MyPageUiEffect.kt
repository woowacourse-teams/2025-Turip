package com.on.turip.ui.compose.mypage

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.setting.model.InquiryMail

sealed interface MyPageUiEffect {
    data object ShowBookmarkRemoveFailed : MyPageUiEffect

    data object NavigateToLogin : MyPageUiEffect

    data class NavigateToInquiry(
        val mail: InquiryMail,
    ) : MyPageUiEffect

    data class NavigateToPrivacyPolicy(
        val url: String,
    ) : MyPageUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: MyPageRetryAction,
    ) : MyPageUiEffect
}

enum class MyPageRetryAction {
    LOGOUT,
    WITHDRAW,
}
