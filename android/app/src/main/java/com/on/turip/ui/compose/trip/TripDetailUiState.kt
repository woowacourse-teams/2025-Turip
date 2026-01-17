package com.on.turip.ui.compose.trip

import androidx.compose.runtime.Stable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.model.DayModel
import com.on.turip.ui.compose.trip.model.PlaceModel
import com.on.turip.ui.compose.trip.model.TripDetailInfoModel

@Stable
data class TripDetailUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val days: List<DayModel>,
    val places: List<PlaceModel>,
    val tripDetailInfo: TripDetailInfoModel,
    val isFavorite: Boolean,
) {
    companion object {
        val IDLE: TripDetailUiState =
            TripDetailUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                days = emptyList(),
                places = emptyList(),
                tripDetailInfo = TripDetailInfoModel.Idle,
                isFavorite = false,
            )
    }
}
