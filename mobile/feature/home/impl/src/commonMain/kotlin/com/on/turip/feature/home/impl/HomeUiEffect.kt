package com.on.turip.feature.home.impl

sealed interface HomeUiEffect {
    data object NavigateToLogin : HomeUiEffect
}
