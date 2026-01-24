package com.on.turip.ui.compose.favorite.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DeletePlaceSnapshot(
    val deletePlace: FavoritePlaceModel,
    val originPlaces: ImmutableList<FavoritePlaceModel>,
) {
    fun hasSnapshot(): Boolean = this != EMPTY

    companion object {
        val EMPTY =
            DeletePlaceSnapshot(
                deletePlace = FavoritePlaceModel(0L, 0L, 0L, "", false, LatLng(0.0, 0.0), "", ""),
                originPlaces = persistentListOf(),
            )
    }
}
