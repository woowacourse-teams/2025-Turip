package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.favorite.model.FavoritePlaceModel
import kotlinx.collections.immutable.ImmutableList

sealed interface FavoritePlaceFolderUiEffect {
    data object NavigateToLogin : FavoritePlaceFolderUiEffect

    data object FolderShareNotAllowed : FavoritePlaceFolderUiEffect

    data class ShowFavoritePlaceRemoveFailed(
        val placeName: String,
    ) : FavoritePlaceFolderUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : FavoritePlaceFolderUiEffect

    data class ShowFavoritePlaceRemoved(
        val placeName: String,
    ) : FavoritePlaceFolderUiEffect

    data class ShowReorderPlaceFailed(
        val retryAction: FavoritePlaceFolderRetryAction,
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

    data class UpdateReorderedPlaces(
        val reorderedPlaces: ImmutableList<FavoritePlaceModel>,
    ) : FavoritePlaceFolderRetryAction
}
