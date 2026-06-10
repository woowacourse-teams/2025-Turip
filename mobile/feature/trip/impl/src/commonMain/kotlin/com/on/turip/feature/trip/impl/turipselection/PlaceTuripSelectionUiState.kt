package com.on.turip.feature.trip.impl.turipselection

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PlaceTuripSelectionUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val placeId: Long,
    val placeName: String,
    val turips: ImmutableList<TuripSelectionModel>,
    val isChanged: Boolean,
    val showAddTuripBottomSheet: Boolean = false,
    val isCreatingTurip: Boolean = false,
    val addTuripInputName: String = "",
    val addTuripNameStatus: TuripNameStatusModel = TuripNameStatusModel.EMPTY,
) {
    companion object {
        val Idle =
            PlaceTuripSelectionUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                placeId = 0L,
                placeName = "",
                turips = persistentListOf(),
                isChanged = false,
            )
    }
}
