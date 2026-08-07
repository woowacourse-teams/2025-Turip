package com.on.turip.feature.trip.impl

import androidx.compose.runtime.Stable
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.feature.trip.impl.model.PlaceModel
import com.on.turip.feature.trip.impl.model.SelectedPlaceModel
import com.on.turip.feature.trip.impl.model.TripDetailInfoModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TripDetailUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val places: ImmutableList<PlaceModel>,
    val tripDetailInfo: TripDetailInfoModel,
    val isBookmarked: Boolean,
    val selectedPlaceModel: SelectedPlaceModel?,
    val showAddTuripBottomSheet: Boolean = false,
    val isCreatingTurip: Boolean = false,
    val addTuripInputName: String = "",
    val addTuripNameStatus: TuripNameStatusModel = TuripNameStatusModel.EMPTY,
) {
    companion object {
        val IDLE: TripDetailUiState =
            TripDetailUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                places = persistentListOf(),
                tripDetailInfo = TripDetailInfoModel.Idle,
                isBookmarked = false,
                selectedPlaceModel = null,
            )
    }
}
