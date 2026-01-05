package com.on.turip.ui.compose.login.model

sealed interface LoginUiEffect {
    data object NavigateToMain : LoginUiEffect
}
