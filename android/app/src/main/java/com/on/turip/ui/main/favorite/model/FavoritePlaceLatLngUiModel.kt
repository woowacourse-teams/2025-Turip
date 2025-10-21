package com.on.turip.ui.main.favorite.model

import com.google.android.gms.maps.model.LatLng

data class FavoritePlaceLatLngUiModel(
    val placeId: Long,
    val name: String,
    val favoriteLatLng: LatLng,
)
