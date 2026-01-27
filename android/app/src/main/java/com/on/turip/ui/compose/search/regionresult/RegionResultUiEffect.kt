package com.on.turip.ui.compose.search.regionresult

sealed interface RegionResultUiEffect {
    data object NavigateToLogin : RegionResultUiEffect
}
