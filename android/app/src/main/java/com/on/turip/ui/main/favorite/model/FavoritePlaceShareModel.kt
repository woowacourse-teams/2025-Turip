package com.on.turip.ui.main.favorite.model

import android.net.Uri

data class FavoritePlaceShareModel(
    val name: String,
    val uri: Uri,
) {
    fun toShareFormat(): String = "장소명 : $name \n지도 링크 : $uri \n"
}
