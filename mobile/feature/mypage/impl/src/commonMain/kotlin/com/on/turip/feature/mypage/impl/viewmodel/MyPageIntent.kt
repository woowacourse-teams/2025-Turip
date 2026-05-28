package com.on.turip.feature.mypage.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface MyPageIntent : UiIntent {
    data object LoadProfile : MyPageIntent
    data object LoadBookmarks : MyPageIntent
    data class RemoveBookmark(val contentId: Long) : MyPageIntent
    data class RollbackRemove(val contentId: Long) : MyPageIntent
    data object ClickInquiry : MyPageIntent
    data object ClickPrivacyPolicy : MyPageIntent
    data object ClickLogout : MyPageIntent
    data object ClickWithdraw : MyPageIntent
    data object ConfirmLogout : MyPageIntent
    data object ConfirmWithdraw : MyPageIntent
    data object DismissDialog : MyPageIntent
}
