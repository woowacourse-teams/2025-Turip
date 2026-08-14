package com.on.turip.feature.trip.impl

import com.on.turip.core.ui.error.ErrorUiState

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

    data class ShowAddedError(
        val errorUiState: ErrorUiState,
        val retryAction: TripDetailRetryAction,
    ) : TripDetailUiEffect

    data class TuripAdded(
        val turipName: String,
    ) : TripDetailUiEffect
}

sealed interface TripDetailRetryAction {
    data object UpdateBookmark : TripDetailRetryAction

    data object AddTurip : TripDetailRetryAction
}
