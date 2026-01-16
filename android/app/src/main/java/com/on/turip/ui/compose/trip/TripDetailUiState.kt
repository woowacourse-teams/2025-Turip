package com.on.turip.ui.compose.trip

import androidx.compose.runtime.Stable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.model.DayModel
import com.on.turip.ui.compose.trip.model.PlaceModel
import com.on.turip.ui.trip.detail.TripDetailInfoModel

@Stable
data class TripDetailUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val days: List<DayModel>,
    val places: List<PlaceModel>,
    val tripDetailInfo: TripDetailInfoModel,
    val isFavorite: Boolean,
    val isExpandTextToggleVisible: Boolean?,
    val isExpandTextToggleSelected: Boolean,
    val isVideoLoaded: Boolean,
) {
    val titleMaxLines: Int
        get() = if (isExpandTextToggleSelected) EXPAND_TEXT else DEFAULT_CONTENT_TITLE_MAX_LINES

    fun updateExpandTextToggleVisibility(
        lineCount: Int,
        ellipsisCount: Int,
    ): TripDetailUiState = copy(isExpandTextToggleVisible = lineCount >= DEFAULT_CONTENT_TITLE_MAX_LINES && ellipsisCount > 0)

    companion object {
        private const val DEFAULT_CONTENT_TITLE_MAX_LINES = 2
        private const val EXPAND_TEXT = Int.MAX_VALUE

        val IDLE: TripDetailUiState =
            TripDetailUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                days = emptyList(),
                places = emptyList(),
                tripDetailInfo = TripDetailInfoModel.Idle,
                isFavorite = false,
                isExpandTextToggleVisible = null,
                isExpandTextToggleSelected = false,
                isVideoLoaded = false,
            )
    }
}
