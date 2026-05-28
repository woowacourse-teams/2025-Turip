package com.on.turip.feature.login.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface LoginEffect : UiEffect {
    data object NavigateToHome : LoginEffect
    data class NavigateToInvitationEntry(val deepLinkUrl: String) : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}
