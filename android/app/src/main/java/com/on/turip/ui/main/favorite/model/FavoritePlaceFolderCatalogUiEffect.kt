package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FavoritePlaceFolderCatalogUiEffect {
    data object NavigateToLogin : FavoritePlaceFolderCatalogUiEffect

    data object ShowFolderShareNotAllowed : FavoritePlaceFolderCatalogUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : FavoritePlaceFolderCatalogUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val action: FavoritePlaceFolderCatalogRetryAction,
    ) : FavoritePlaceFolderCatalogUiEffect
}

sealed interface FavoritePlaceFolderCatalogRetryAction {
    data object LoadPlacesInFolder : FavoritePlaceFolderCatalogRetryAction
}
