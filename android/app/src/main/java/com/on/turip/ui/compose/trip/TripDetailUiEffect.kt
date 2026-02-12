package com.on.turip.ui.compose.trip

import com.on.turip.ui.common.error.ErrorUiState

sealed interface TripDetailUiEffect {
    data object NavigateToLogin : TripDetailUiEffect

    data class ShowBookmarkStatus(
        val isBookmarked: Boolean,
    ) : TripDetailUiEffect

    data class ShowUpdatedTuripSelectionByPlace(
        val placeName: String,
    ) : TripDetailUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: TripDetailRetryAction,
    ) : TripDetailUiEffect
}

sealed interface TripDetailRetryAction {
    data object UpdateBookmark : TripDetailRetryAction
}
