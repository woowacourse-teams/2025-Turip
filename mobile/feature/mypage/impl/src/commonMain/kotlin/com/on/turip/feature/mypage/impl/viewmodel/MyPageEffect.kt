package com.on.turip.feature.mypage.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface MyPageEffect : UiEffect {
    data object NavigateToLogin : MyPageEffect
    data class ShowRemoveFailedSnackbar(val contentId: Long) : MyPageEffect
    data object ShowBookmarksLoadFailed : MyPageEffect
    data object ShowProfileLoadFailed : MyPageEffect
    data class NavigateToInquiry(val appVersion: String, val deviceId: String) : MyPageEffect
    data class NavigateToPrivacyPolicy(val url: String) : MyPageEffect
    data object ShowLogoutError : MyPageEffect
    data object ShowWithdrawError : MyPageEffect
}
