package com.on.turip.feature.trip.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface TripDetailIntent : UiIntent {
    data object Retry : TripDetailIntent
    data object ToggleBookmark : TripDetailIntent
}
