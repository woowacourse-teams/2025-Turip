package com.on.turip.ui.main.favorite.model

sealed interface FavoritePlaceUiEffect {
    data object ShowFolderShareNotAllowed : FavoritePlaceUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : FavoritePlaceUiEffect
}
