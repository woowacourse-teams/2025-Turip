package com.on.turip.feature.trip.impl.turipselection

import androidx.compose.runtime.Immutable
import com.on.turip.feature.trip.impl.turipselection.model.TuripPlaceModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PlaceTuripSelectionUiState(
    val selectionPlaceId: Long,
    val screenMode: PlaceTuripSelectionScreenMode,
    val placeName: String,
    val turips: ImmutableList<TuripSelectionModel>,
    val selectedTuripPlaces: ImmutableList<TuripPlaceModel>,
    val isChanged: Boolean,
    val isUpdatingTurips: Boolean = false,
) {
    companion object {
        val Idle =
            PlaceTuripSelectionUiState(
                selectionPlaceId = 0L,
                screenMode = PlaceTuripSelectionScreenMode.Turips,
                placeName = "",
                turips = persistentListOf(),
                selectedTuripPlaces = persistentListOf(),
                isChanged = false,
                isUpdatingTurips = false,
            )
    }
}

@Immutable
sealed interface PlaceTuripSelectionScreenMode {
    data object Turips : PlaceTuripSelectionScreenMode

    data class TuripDetail(
        val turipModel: TuripSelectionModel,
    ) : PlaceTuripSelectionScreenMode
}
