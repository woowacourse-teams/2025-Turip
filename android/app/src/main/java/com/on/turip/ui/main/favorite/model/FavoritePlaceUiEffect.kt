package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FavoritePlaceUiEffect {
    data object NavigateToLogin : FavoritePlaceUiEffect

    data object ShowFolderShareNotAllowed : FavoritePlaceUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : FavoritePlaceUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : FavoritePlaceUiEffect
}
