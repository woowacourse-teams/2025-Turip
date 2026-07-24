package com.on.turip.feature.mypage.impl

sealed interface MyPageDialogState {
    data object LogoutRequired : MyPageDialogState

    data object ConfirmWithdraw : MyPageDialogState
}
