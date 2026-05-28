package com.on.turip.feature.turipdetail.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface TuripDetailEffect : UiEffect {
    data object NavigateBack : TuripDetailEffect
    data object NavigateToLogin : TuripDetailEffect
    data object ShowError : TuripDetailEffect
    data class ShowPlaceRemoved(val placeName: String) : TuripDetailEffect
}
