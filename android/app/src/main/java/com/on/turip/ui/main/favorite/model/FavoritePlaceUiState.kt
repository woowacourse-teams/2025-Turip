package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

data class FavoritePlaceUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val places: List<FavoritePlaceUiModel>,
    val folders: List<FavoritePlaceFolderModel>,
    val placesLatLng: List<FavoritePlaceLatLngUiModel>,
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
