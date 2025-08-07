package com.on.turip.ui.trip.detail

import android.net.Uri
import androidx.core.net.toUri

data class PlaceModel(
    val name: String,
    val category: String,
    val mapLink: String,
) {
    val placeUri: Uri
        get() = mapLink.toUri()
    val turipCategory: String
        get() = parseCategory()

    private fun parseCategory(): String {
        val findIndex: Int = category.indexOfLast { it == '>' }
        if (findIndex == -1) return category
        return category.substring(findIndex + 1).trim()
    }
}
