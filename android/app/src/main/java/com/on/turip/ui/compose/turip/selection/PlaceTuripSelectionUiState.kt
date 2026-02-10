package com.on.turip.ui.compose.turip.selection

import androidx.compose.runtime.Immutable
import com.on.turip.ui.compose.turip.selection.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.TuripModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
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

@Immutable
sealed interface PlaceTuripSelectionScreenMode {
    data object Turips : PlaceTuripSelectionScreenMode

    data class TuripDetail(
        val turipId: Long,
        val turipName: String,
    ) : PlaceTuripSelectionScreenMode
}
