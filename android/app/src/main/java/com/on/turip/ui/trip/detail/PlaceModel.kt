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
}
