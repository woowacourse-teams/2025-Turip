package com.on.turip.feature.search.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface SearchEffect : UiEffect {
    data object NavigateToLogin : SearchEffect
    data object ClearFocus : SearchEffect
}
