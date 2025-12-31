package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FavoriteContentUiEffect {
    data object NavigateToLogin : FavoriteContentUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: () -> Unit
    ) : FavoriteContentUiEffect
}
