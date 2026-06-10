package com.on.turip.feature.turipdetail.impl.model.turip

import androidx.compose.runtime.Immutable

@Immutable
data class PlaceLatLngUiModel(
    val placeId: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        val Idle = PlaceLatLngUiModel(0L, "", 0.0, 0.0)
    }
}
