package com.on.turip.ui.main.favorite.model

data class TuripPlaceFolderCatalogUiState(
    val places: List<TuripPlaceModel>,
    val folderName: String,
) {
    companion object {
        val Idle: TuripPlaceFolderCatalogUiState =
            TuripPlaceFolderCatalogUiState(
                places = emptyList(),
                folderName = "",
            )
    }
}
