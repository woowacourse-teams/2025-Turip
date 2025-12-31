package com.on.turip.ui.main.favorite.model

import com.on.turip.data.common.ErrorUiState

sealed interface FavoriteContentUiEffect {
    data object NavigateToLogin : FavoriteContentUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : FavoriteContentUiEffect
}
