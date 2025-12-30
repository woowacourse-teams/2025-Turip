package com.on.turip.ui.main.favorite.model

sealed interface FavoritePlaceFolderUiEffect {
    data class ShowUpdateFavoriteState(
        val folder: FavoritePlaceFolderModel,
    ) : FavoritePlaceFolderUiEffect
}
