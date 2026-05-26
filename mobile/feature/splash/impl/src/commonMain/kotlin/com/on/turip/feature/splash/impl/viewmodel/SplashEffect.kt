package com.on.turip.feature.splash.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface SplashEffect : UiEffect {
    data object NavigateToMain : SplashEffect

    data object NavigateToLogin : SplashEffect

    data class NavigateToInvitationEntry(
        val deepLinkUrl: String,
    ) : SplashEffect
}
