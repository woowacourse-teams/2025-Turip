package com.on.turip.ui.splash

sealed interface SplashUiEffect {
    data object NavigateToMain : SplashUiEffect

    data object NavigateToLogin : SplashUiEffect
}
