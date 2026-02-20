package com.on.turip.ui.compose.trip.turipselection.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DeletePlaceSnapshot(
    val deletePlace: TuripPlaceModel,
    val originPlaces: ImmutableList<TuripPlaceModel>,
) {
    fun hasSnapshot(): Boolean = this != EMPTY

    companion object {
        val EMPTY =
            DeletePlaceSnapshot(
                deletePlace = TuripPlaceModel(0L, 0L, 0L, "", false, LatLng(0.0, 0.0), "", ""),
                originPlaces = persistentListOf(),
            )
    }
}
