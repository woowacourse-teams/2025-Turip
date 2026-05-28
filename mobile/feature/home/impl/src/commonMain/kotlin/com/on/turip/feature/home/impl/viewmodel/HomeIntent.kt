package com.on.turip.feature.home.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface HomeIntent : UiIntent {
    data object LoadContents : HomeIntent
    data class SelectDomestic(val isDomestic: Boolean) : HomeIntent
}
