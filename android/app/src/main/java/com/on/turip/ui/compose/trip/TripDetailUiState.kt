package com.on.turip.ui.compose.trip

import androidx.compose.runtime.Stable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.model.DayModel
import com.on.turip.ui.compose.trip.model.PlaceModel
import com.on.turip.ui.compose.trip.model.TripDetailInfoModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TripDetailUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val days: ImmutableList<DayModel>,
    val places: ImmutableList<PlaceModel>,
    val tripDetailInfo: TripDetailInfoModel,
    val isFavorite: Boolean,
) {
    companion object {
        val IDLE: TripDetailUiState =
            TripDetailUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                days = persistentListOf(),
                places = persistentListOf(),
                tripDetailInfo = TripDetailInfoModel.Idle,
                isFavorite = false,
            )
    }
}
