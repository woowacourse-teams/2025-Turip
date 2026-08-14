package com.on.turip.core.ui.model.turip

data class TuripPlaceShareModel(
    val name: String,
    val uri: String,
) {
    fun toShareFormat(): String = "$PLACE_NAME_EMOJI_UNICODE 장소명 : $name \n${MAP_LINK_EMOJI_UNICODE} 지도 링크 : $uri \n"

    companion object {
        private const val PLACE_NAME_EMOJI_UNICODE = "\uD83D\uDCCD"
        private const val MAP_LINK_EMOJI_UNICODE = "\uD83D\uDD17"
    }
}
