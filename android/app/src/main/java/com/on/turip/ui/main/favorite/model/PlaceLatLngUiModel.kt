package com.on.turip.ui.main.favorite.model

import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.model.LatLng

@Immutable
data class PlaceLatLngUiModel(
    val placeId: Long,
    val name: String,
    val latLng: LatLng,
)
