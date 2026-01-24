package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FavoritePlaceFolderUiEffect {
    data object NavigateToLogin : FavoritePlaceFolderUiEffect

    data object FolderShareNotAllowed : FavoritePlaceFolderUiEffect

    data object DeletePlaceFailed : FavoritePlaceFolderUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : FavoritePlaceFolderUiEffect

    data class ShowRemovedFavoritePlace(
        val placeName: String,
    ) : FavoritePlaceFolderUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: FavoritePlaceFolderRetryAction,
    ) : FavoritePlaceFolderUiEffect
}

sealed interface FavoritePlaceFolderRetryAction {
    data object LoadFavoriteFolders : FavoritePlaceFolderRetryAction

    data class UpdateFolder(
        val favoritePlaceFolderModel: FavoritePlaceFolderModel,
    ) : FavoritePlaceFolderRetryAction

    data class LoadFavoritePlacesInFolder(
        val folderId: Long,
        val folderName: String,
    ) : FavoritePlaceFolderRetryAction
}
