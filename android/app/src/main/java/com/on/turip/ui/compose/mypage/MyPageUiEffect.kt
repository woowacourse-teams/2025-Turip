package com.on.turip.ui.compose.mypage

sealed interface MyPageUiEffect {
    data object ShowBookmarkRemoveFailed : MyPageUiEffect
}
