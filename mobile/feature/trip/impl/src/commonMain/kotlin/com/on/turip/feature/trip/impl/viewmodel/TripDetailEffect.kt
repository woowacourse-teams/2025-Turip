package com.on.turip.feature.trip.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface TripDetailEffect : UiEffect {
    data object NavigateToLogin : TripDetailEffect
    data class ShowBookmarkStatus(val isBookmarked: Boolean) : TripDetailEffect
    data object ShowBookmarkError : TripDetailEffect
    data class OpenVideoUrl(val url: String) : TripDetailEffect
}
