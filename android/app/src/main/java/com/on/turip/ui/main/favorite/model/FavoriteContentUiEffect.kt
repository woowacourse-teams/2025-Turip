package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FavoriteContentUiEffect {
    data object NavigateToLogin : FavoriteContentUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val action: FavoriteContentRetryAction,
    ) : FavoriteContentUiEffect
}

sealed interface FavoriteContentRetryAction {
    data class UpdateFavorite(
        val contentId: Long,
        val isFavorite: Boolean,
    ) : FavoriteContentRetryAction
}
