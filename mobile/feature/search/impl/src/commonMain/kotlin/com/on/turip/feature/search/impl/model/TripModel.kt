package com.on.turip.feature.search.impl.model

import com.on.turip.core.model.trip.ContentPlace

data class TripModel(
    val tripDurationModel: TripDurationModel,
    val tripPlaceCount: Int,
    val contentPlaces: List<ContentPlace> = emptyList(),
)
