package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

data class FavoritePlaceUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val places: List<TuripPlaceUiModel>,
    val folders: List<TuripModel>,
    val placesLatLng: List<PlaceLatLngUiModel>,
) {
    val isEmpty: Boolean
        get() = places.isEmpty() && this != Idle

    companion object {
        val Idle: FavoritePlaceUiState =
            FavoritePlaceUiState(
                isLoading = true,
                errorUiState = ErrorUiState.None,
                places = emptyList(),
                folders = emptyList(),
                placesLatLng = emptyList(),
            )
    }
}
