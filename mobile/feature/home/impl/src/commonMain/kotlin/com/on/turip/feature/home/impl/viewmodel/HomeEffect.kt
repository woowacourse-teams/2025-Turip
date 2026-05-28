package com.on.turip.feature.home.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface HomeEffect : UiEffect {
    data object NavigateToLogin : HomeEffect
}
