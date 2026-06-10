package com.on.turip.feature.turipdetail.impl.model

data class TuripPlaceModel(
    val turipPlaceId: Long,
    val placeId: Long,
    val name: String,
    val category: String,
    val mapModel: MapModel,
    val isTuripPlace: Boolean,
) {
    val turipCategory: String
        get() = category

    companion object {
        val Idle =
            TuripPlaceModel(
                turipPlaceId = 0L,
                placeId = 0L,
                name = "",
                category = "",
                mapModel = MapModel.from(""),
                isTuripPlace = false,
            )
    }
}
