package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.compose.turip.selection.model.TuripPlaceModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PlaceTuripSelectionUiState(
    val screenMode: PlaceTuripSelectionScreenMode,
    val placeName: String,
    val turips: ImmutableList<TuripModel>,
    val selectedTuripPlaces: ImmutableList<TuripPlaceModel>,
    val isChanged: Boolean,
) {
    val hasTuripForPlace: Boolean
        get() = turips.any { it.isSelected }

    companion object {
        val Idle: PlaceTuripSelectionUiState =
            PlaceTuripSelectionUiState(
                screenMode = PlaceTuripSelectionScreenMode.Turips,
                placeName = "",
                turips = persistentListOf(),
                selectedTuripPlaces = persistentListOf(),
                isChanged = false,
            )
    }
}

sealed interface PlaceTuripSelectionScreenMode {
    data object Turips : PlaceTuripSelectionScreenMode

    data class TuripDetail(
        val turipId: Long,
        val turipName: String,
    ) : PlaceTuripSelectionScreenMode
}
