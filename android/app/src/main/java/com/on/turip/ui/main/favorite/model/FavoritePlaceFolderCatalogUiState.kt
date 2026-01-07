package com.on.turip.ui.main.favorite.model

data class FavoritePlaceFolderCatalogUiState(
    val places: List<FavoritePlaceModel>,
    val folderName: String,
) {
    companion object {
        val Idle: FavoritePlaceFolderCatalogUiState =
            FavoritePlaceFolderCatalogUiState(
                places = emptyList(),
                folderName = "",
            )
    }
}
