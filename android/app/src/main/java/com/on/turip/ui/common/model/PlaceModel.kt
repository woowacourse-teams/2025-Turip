package com.on.turip.ui.common.model

import android.net.Uri
import androidx.core.net.toUri

data class PlaceModel(
    val name: String,
    val category: String,
    val mapLink: String,
) {
    fun placeUri(): Uri = mapLink.toUri()
}
