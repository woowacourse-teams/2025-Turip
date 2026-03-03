package com.on.turip.ui.compose.turipdetail.model.turip

import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.model.LatLng

@Immutable
data class PlaceLatLngUiModel(
    val placeId: Long,
    val name: String,
    val latLng: LatLng,
) {
    companion object {
        val Idle = PlaceLatLngUiModel(0L, "", LatLng(0.0, 0.0))
    }
}
