package com.on.turip.ui.compose.trip

import androidx.compose.runtime.Stable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.model.PlaceModel
import com.on.turip.ui.compose.trip.model.TripDetailInfoModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TripDetailUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val places: ImmutableList<PlaceModel>,
    val tripDetailInfo: TripDetailInfoModel,
    val isBookmarked: Boolean,
) {
    companion object {
        val IDLE: TripDetailUiState =
            TripDetailUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                places = persistentListOf(),
                tripDetailInfo = TripDetailInfoModel.Idle,
                isBookmarked = false,
            )
    }
}
