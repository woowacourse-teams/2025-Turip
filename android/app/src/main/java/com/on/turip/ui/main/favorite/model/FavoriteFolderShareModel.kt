package com.on.turip.ui.main.favorite.model

data class FavoriteFolderShareModel(
    val name: String,
    val places: List<TuripPlaceShareModel>,
) {
    fun toShareFormat(): String =
        buildString {
            appendLine("$FOLDER_NAME_EMOJI_UNICODE 폴더명 : $name")
            appendLine()
            places.forEach { placeShareModel: TuripPlaceShareModel -> appendLine(placeShareModel.toShareFormat()) }
            appendLine("$DOWNLOAD_EMOJI_UNICODE 튜립 플레이스토어 다운로드 링크 : $TURIP_PLAY_STORE_DOWNLOAD_LINK")
        }

    companion object {
        private const val FOLDER_NAME_EMOJI_UNICODE = "\uD83D\uDCC1"
        private const val DOWNLOAD_EMOJI_UNICODE = "\uD83D\uDCE5"
        private const val TURIP_PLAY_STORE_DOWNLOAD_LINK =
            "https://play.google.com/store/apps/details?id=com.on.turip&hl=ko"
    }
}
