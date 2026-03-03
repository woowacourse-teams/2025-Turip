package com.on.turip.ui.compose.mypage

sealed interface MyPageDialogState {
    data object LogoutRequired : MyPageDialogState

    data object ConfirmWithdraw : MyPageDialogState
}
