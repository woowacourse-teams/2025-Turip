package com.on.turip.ui.compose.favorite.model.turip

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class TuripPlaceUiModel(
    val turipPlaceId: Long,
    val order: Long,
    val placeId: Long,
    val name: String,
    val uri: Uri,
    val category: String,
    val isTuripPlace: Boolean,
    val latLng: LatLng,
) {
    val turipCategory: String
        get() = parseCategory()

    private fun parseCategory(): String {
        val findIndex: Int = category.indexOfLast { it == '>' }
        if (findIndex == -1) return category
        return category.substring(findIndex + 1).trim()
    }
}
