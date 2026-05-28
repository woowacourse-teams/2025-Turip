package com.on.turip.feature.turip.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface MyTuripEffect : UiEffect {
    data object NavigateToLogin : MyTuripEffect
    data class ShowTuripAdded(val name: String) : MyTuripEffect
    data class ShowTuripDeleted(val name: String) : MyTuripEffect
    data object ShowLoadError : MyTuripEffect
    data object ShowActionError : MyTuripEffect
}
