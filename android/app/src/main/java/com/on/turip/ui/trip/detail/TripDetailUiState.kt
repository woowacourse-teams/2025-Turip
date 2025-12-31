package com.on.turip.ui.trip.detail

import com.on.turip.ui.common.error.ErrorUiState

data class TripDetailUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val days: List<DayModel>,
    val places: List<PlaceModel>,
    val tripDetailInfo: TripDetailInfoModel?,
    val isFavorite: Boolean,
    val isExpandTextToggleVisible: Boolean?,
    val isExpandTextToggleSelected: Boolean,
) {
    val titleMaxLines: Int
        get() = if (isExpandTextToggleSelected) EXPAND_TEXT else DEFAULT_CONTENT_TITLE_MAX_LINES

    fun updateExpandTextToggleVisibility(
        lineCount: Int,
        ellipsisCount: Int,
    ): TripDetailUiState =
        copy(isExpandTextToggleVisible = lineCount >= DEFAULT_CONTENT_TITLE_MAX_LINES && ellipsisCount > 0)

    companion object {
        private const val DEFAULT_CONTENT_TITLE_MAX_LINES = 2
        private const val EXPAND_TEXT = Int.MAX_VALUE

        val IDLE: TripDetailUiState =
            TripDetailUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                days = emptyList(),
                places = emptyList(),
                tripDetailInfo = null,
                isFavorite = false,
                isExpandTextToggleVisible = null,
                isExpandTextToggleSelected = false,
            )
    }
}
