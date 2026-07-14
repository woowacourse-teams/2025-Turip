package com.on.turip.feature.trip.impl.turipselection.model

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
                deletePlace = TuripPlaceModel.Idle,
                originPlaces = persistentListOf(),
            )
    }
}
