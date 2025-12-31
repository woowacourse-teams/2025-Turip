package com.on.turip.ui.compose.home

sealed interface HomeUiEffect {
    data object NavigateToLogin : HomeUiEffect
}
