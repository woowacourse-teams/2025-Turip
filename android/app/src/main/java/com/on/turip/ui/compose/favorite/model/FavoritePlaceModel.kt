package com.on.turip.ui.compose.favorite.model

import androidx.compose.runtime.Stable
import com.google.android.gms.maps.model.LatLng
import com.on.turip.ui.compose.trip.model.MapModel

@Stable
data class FavoritePlaceModel(
    val favoritePlaceId: Long,
    val order: Long,
    val placeId: Long,
    val name: String,
    val isFavorite: Boolean,
    val latLng: LatLng,
    private val category: String,
    private val mapLink: String,
) {
    val mapModel: MapModel = MapModel.from(mapLink)

    val turipCategory: String
        get() {
            val findIndex: Int = category.indexOfLast { it == '>' }
            if (findIndex == -1) return category
            return category.substring(findIndex + 1).trim()
        }
}
