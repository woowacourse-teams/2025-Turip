package com.on.turip.ui.main.favorite.model

import com.on.turip.data.common.ErrorUiState

sealed interface FavoritePlaceFolderCatalogUiEffect {
    data object NavigateToLogin : FavoritePlaceFolderCatalogUiEffect

    data object ShowFolderShareNotAllowed : FavoritePlaceFolderCatalogUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : FavoritePlaceFolderCatalogUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : FavoritePlaceFolderCatalogUiEffect
}
