package com.on.turip.ui.main.favorite.model

data class FavoriteFolderShareModel(
    val name: String,
    val places: List<FavoritePlaceShareModel>,
) {
    fun toShareFormat(): String =
        buildString {
            places.forEach { placeShareModel: FavoritePlaceShareModel -> appendLine(placeShareModel.toShareFormat()) }
        }
}
