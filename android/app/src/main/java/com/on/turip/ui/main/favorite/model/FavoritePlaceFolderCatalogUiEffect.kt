package com.on.turip.ui.main.favorite.model

sealed interface FavoritePlaceFolderCatalogUiEffect {
    data object ShowFolderShareNotAllowed : FavoritePlaceFolderCatalogUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : FavoritePlaceFolderCatalogUiEffect
}
