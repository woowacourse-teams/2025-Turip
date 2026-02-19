package com.on.turip.ui.splash

sealed interface SplashEffect {
    data object NavigateMain : SplashEffect
}
