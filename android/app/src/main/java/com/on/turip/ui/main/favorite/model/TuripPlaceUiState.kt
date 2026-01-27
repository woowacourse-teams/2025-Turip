package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

data class TuripPlaceUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val places: List<TuripPlaceModel>,
    val folders: List<FavoritePlaceFolderModel>,
    val placesLatLng: List<TuripPlaceLatLngUiModel>,
) {
    val isEmpty: Boolean
        get() = places.isEmpty() && this != Idle

    companion object {
        val Idle: TuripPlaceUiState =
            TuripPlaceUiState(
                isLoading = true,
                errorUiState = ErrorUiState.None,
                places = emptyList(),
                folders = emptyList(),
                placesLatLng = emptyList(),
            )
    }
}
