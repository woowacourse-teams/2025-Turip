package com.on.turip.ui.compose.splash

sealed interface SplashUiEffect {
    data object NavigateToMain : SplashUiEffect

    data object NavigateToLogin : SplashUiEffect
}
