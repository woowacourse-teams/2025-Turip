package com.on.turip.ui.trip.detail

import com.on.turip.data.common.ErrorUiState

sealed interface TripDetailUiEffect {
    data object NavigateToLogin : TripDetailUiEffect

    data class ShowFavoriteStatus(
        val isFavorite: Boolean,
    ) : TripDetailUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : TripDetailUiEffect
}
