package com.on.turip.ui.compose.trip

import com.on.turip.ui.common.error.ErrorUiState

sealed interface TripDetailUiEffect {
    data object NavigateToLogin : TripDetailUiEffect

    data class ShowFavoriteStatus(
        val isFavorite: Boolean,
    ) : TripDetailUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: TripDetailRetryAction,
    ) : TripDetailUiEffect
}

sealed interface TripDetailRetryAction {
    data object UpdateFavorite : TripDetailRetryAction
}
