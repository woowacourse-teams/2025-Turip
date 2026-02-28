package com.on.turip.ui.compose.favorite.model

import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DeleteTuripPlaceSnapshot(
    val deletePlace: TuripPlaceModel,
    val originPlaces: ImmutableList<TuripPlaceModel>,
    val originPlacesLatLng: ImmutableList<PlaceLatLngUiModel>,
) {
    val hasSnapshot: Boolean get() = this != EMPTY

    companion object {
        val EMPTY =
            DeleteTuripPlaceSnapshot(
                deletePlace = TuripPlaceModel.Idle,
                originPlaces = persistentListOf(),
                originPlacesLatLng = persistentListOf(),
            )
    }
}
