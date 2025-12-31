package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FavoritePlaceFolderUiEffect {
    data object NavigateToLogin : FavoritePlaceFolderUiEffect

    data class ShowUpdateFavoriteState(
        val folder: FavoritePlaceFolderModel,
    ) : FavoritePlaceFolderUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : FavoritePlaceFolderUiEffect
}
