package com.on.turip.ui.trip.detail

sealed interface TripDetailUiEffect {
    data class ShowFavoriteStatus(
        val isFavorite: Boolean,
    ) : TripDetailUiEffect
}
