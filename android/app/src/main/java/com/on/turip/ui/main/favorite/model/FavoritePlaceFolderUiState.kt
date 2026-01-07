package com.on.turip.ui.main.favorite.model

data class FavoritePlaceFolderUiState(
    val placeId: Long,
    val favoritePlaceFolders: List<FavoritePlaceFolderModel>,
) {
    val hasFavoriteFolder: Boolean
        get() = favoritePlaceFolders.any { it.isSelected }

    companion object {
        val Idle: FavoritePlaceFolderUiState =
            FavoritePlaceFolderUiState(
                placeId = 0L,
                favoritePlaceFolders = emptyList(),
            )
    }
}
