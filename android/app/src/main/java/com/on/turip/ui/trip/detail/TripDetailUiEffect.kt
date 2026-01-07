package com.on.turip.ui.trip.detail

import com.on.turip.ui.common.error.ErrorUiState

sealed interface TripDetailUiEffect {
    data object NavigateToLogin : TripDetailUiEffect

    data class ShowFavoriteStatus(
        val isFavorite: Boolean,
    ) : TripDetailUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val action: TripDetailRetryAction,
    ) : TripDetailUiEffect
}

sealed interface TripDetailRetryAction {
    data object UpdateFavorite : TripDetailRetryAction
}
