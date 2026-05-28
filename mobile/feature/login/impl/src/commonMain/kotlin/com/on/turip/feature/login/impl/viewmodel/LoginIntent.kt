package com.on.turip.feature.login.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface LoginIntent : UiIntent {
    data class ClickGoogleLogin(val idToken: String) : LoginIntent
    data object ClickGuestLogin : LoginIntent
}
