package com.on.turip.feature.turipdetail.impl.model

import com.on.turip.core.ui.model.AppleMap

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

    companion object {
        val Idle =
            TuripPlaceModel(
                turipPlaceId = 0L,
                placeId = 0L,
                order = 0L,
                name = "",
                category = "",
                mapModel = MapModel.from("", AppleMap.Idle),
                isTuripPlace = false,
                latitude = 0.0,
                longitude = 0.0,
            )
    }
}
