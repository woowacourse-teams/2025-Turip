package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

data class TuripPlaceUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val places: List<TuripPlaceUiModel>,
    val turips: List<TuripModel>,
    val placesLatLng: List<PlaceLatLngUiModel>,
) {
    val isEmpty: Boolean
        get() = places.isEmpty() && this != Idle

    companion object {
        val Idle: TuripPlaceUiState =
            TuripPlaceUiState(
                isLoading = true,
                errorUiState = ErrorUiState.None,
                places = emptyList(),
                turips = emptyList(),
                placesLatLng = emptyList(),
            )
    }
}
