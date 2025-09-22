package com.on.turip.ui.main.favorite.model

data class FavoriteFolderShareModel(
    val name: String,
    val places: List<FavoritePlaceShareModel>,
) {
    fun toShareFormat(): String =
        buildString {
            appendLine("폴더명 : $name")
            appendLine()
            places.forEach { placeShareModel: FavoritePlaceShareModel -> appendLine(placeShareModel.toShareFormat()) }
        }
}
