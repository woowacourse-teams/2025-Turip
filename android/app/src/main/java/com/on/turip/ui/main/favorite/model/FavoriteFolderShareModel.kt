package com.on.turip.ui.main.favorite.model

data class FavoriteFolderShareModel(
    val name: String,
    val places: List<FavoritePlaceShareModel>,
) {
    fun toShareFormat(): String =
        buildString {
            appendLine("$FOLDER_NAME_EMOJI_UNICODE 폴더명 : $name")
            appendLine()
            places.forEach { placeShareModel: FavoritePlaceShareModel -> appendLine(placeShareModel.toShareFormat()) }
        }

    companion object {
        private const val FOLDER_NAME_EMOJI_UNICODE = "\uD83D\uDCC1"
    }
}
