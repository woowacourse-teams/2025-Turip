package com.on.turip.ui.compose.splash

import com.on.turip.core.navigation.InitialNavigationTarget

sealed interface SplashUiEffect {
    data class NavigateToMain(
        val initialNavigationTarget: InitialNavigationTarget,
    ) : SplashUiEffect
}
