package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

data class FavoritePlaceUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val places: List<FavoritePlaceModel>,
    val folders: List<FavoritePlaceFolderModel>,
    val placesLatLng: List<FavoritePlaceLatLngUiModel>,
) {
    val isEmpty: Boolean
        get() = places.isEmpty()

    companion object {
        val Idle: FavoritePlaceUiState =
            FavoritePlaceUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                places = emptyList(),
                folders = emptyList(),
                placesLatLng = emptyList(),
            )
    }
}
