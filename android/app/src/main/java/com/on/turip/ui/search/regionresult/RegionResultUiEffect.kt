package com.on.turip.ui.search.regionresult

sealed interface RegionResultUiEffect {
    data object NavigateToLogin : RegionResultUiEffect
}