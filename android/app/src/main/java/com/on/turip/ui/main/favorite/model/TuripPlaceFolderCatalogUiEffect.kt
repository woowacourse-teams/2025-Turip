package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface TuripPlaceFolderCatalogUiEffect {
    data object NavigateToLogin : TuripPlaceFolderCatalogUiEffect

    data object ShowFolderShareNotAllowed : TuripPlaceFolderCatalogUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : TuripPlaceFolderCatalogUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val action: TuripPlaceFolderCatalogRetryAction,
    ) : TuripPlaceFolderCatalogUiEffect
}

sealed interface TuripPlaceFolderCatalogRetryAction {
    data object LoadPlacesInFolder : TuripPlaceFolderCatalogRetryAction
}
