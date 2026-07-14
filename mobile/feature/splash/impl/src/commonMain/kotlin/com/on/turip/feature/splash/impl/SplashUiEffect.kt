package com.on.turip.feature.splash.impl

sealed interface SplashUiEffect {
    data object NavigateToMain : SplashUiEffect

    data object NavigateToLogin : SplashUiEffect

    data class NavigateToInvitationEntry(
        val deepLinkUrl: String,
    ) : SplashUiEffect
}
