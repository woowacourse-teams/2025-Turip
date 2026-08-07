package com.on.turip.feature.mypage.impl

import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.mypage.impl.model.InquiryMail

sealed interface MyPageUiEffect {
    data class ShowBookmarkRemoveFailed(
        val contentId: Long,
    ) : MyPageUiEffect

    data object ShowBookmarksLoadFailed : MyPageUiEffect

    data object ShowProfileLoadFailed : MyPageUiEffect

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
