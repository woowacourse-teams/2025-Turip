package com.on.turip.feature.trip.impl.turipselection.model

import com.on.turip.core.ui.model.turip.TuripPlaceShareModel
import com.on.turip.feature.trip.impl.model.MapModel

data class TuripPlaceModel(
    val turipPlaceId: Long,
    val placeId: Long,
    val order: Long,
    val name: String,
    val category: String,
    val mapModel: MapModel,
    val isTuripPlace: Boolean,
    val latitude: Double,
    val longitude: Double,
) {
    val turipCategory: String
        get() = category

    fun toShareModel(): TuripPlaceShareModel =
        TuripPlaceShareModel(
            name = name,
            uri = mapModel.uri,
        )

    companion object {
        val Idle =
            TuripPlaceModel(
                turipPlaceId = 0L,
                placeId = 0L,
                order = 0L,
                name = "",
                category = "",
                mapModel = MapModel.from(""),
                isTuripPlace = false,
                latitude = 0.0,
                longitude = 0.0,
            )
    }
}
