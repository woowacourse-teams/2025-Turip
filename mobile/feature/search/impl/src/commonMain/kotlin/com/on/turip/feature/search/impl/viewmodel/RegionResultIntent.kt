package com.on.turip.feature.search.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface RegionResultIntent : UiIntent {
    data object Retry : RegionResultIntent
}
