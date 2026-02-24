package com.on.turip.ui.compose.favorite

import androidx.compose.runtime.Immutable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TuripPlaceUiState(
    val isLoading: Boolean,
    val selectedTuripId: Long,
    val selectedTuripName: String,
    val inputTuripName: String,
    val errorUiState: ErrorUiState,
    val showBottomSheet: Boolean,
    val places: ImmutableList<TuripPlaceModel>,
    val placesLatLng: ImmutableList<PlaceLatLngUiModel>,
) {
    val isEmpty: Boolean
        get() = places.isEmpty() && this != Idle

    companion object {
        val Idle: TuripPlaceUiState =
            TuripPlaceUiState(
                isLoading = true,
                selectedTuripId = 0L,
                selectedTuripName = "",
                inputTuripName = "",
                errorUiState = ErrorUiState.None,
                showBottomSheet = false,
                places = persistentListOf(),
                placesLatLng = persistentListOf(),
            )
    }
}
