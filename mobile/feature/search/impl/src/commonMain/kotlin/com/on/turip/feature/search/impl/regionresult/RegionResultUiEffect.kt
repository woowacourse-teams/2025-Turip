package com.on.turip.feature.search.impl.regionresult

sealed interface RegionResultUiEffect {
    data object NavigateToLogin : RegionResultUiEffect
}
