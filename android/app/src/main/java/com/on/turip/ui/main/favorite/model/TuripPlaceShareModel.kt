package com.on.turip.ui.main.favorite.model

import android.net.Uri

data class TuripPlaceShareModel(
    val name: String,
    val uri: Uri,
) {
    fun toShareFormat(): String = "$PLACE_NAME_EMOJI_UNICODE 장소명 : $name \n${MAP_LINK_EMOJI_UNICODE} 지도 링크 : $uri \n"

    companion object {
        private const val PLACE_NAME_EMOJI_UNICODE = "\uD83D\uDCCD"
        private const val MAP_LINK_EMOJI_UNICODE = "\uD83D\uDD17"
    }
}
