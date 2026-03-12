package com.on.turip.ui.compose.trip.turipselection

import androidx.compose.runtime.Immutable
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.TuripModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PlaceTuripSelectionUiState(
    val selectionPlaceId: Long,
    val screenMode: PlaceTuripSelectionScreenMode,
    val placeName: String,
    val turips: ImmutableList<TuripModel>,
    val selectedTuripPlaces: ImmutableList<TuripPlaceModel>,
    val isChanged: Boolean,
) {
    companion object {
        val Idle: PlaceTuripSelectionUiState =
            PlaceTuripSelectionUiState(
                selectionPlaceId = 0L,
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
        val turipModel: TuripModel,
    ) : PlaceTuripSelectionScreenMode
}
