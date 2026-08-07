package com.on.turip.core.model.trip

data class Trip(
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
    val contentPlaces: List<ContentPlace> = emptyList<ContentPlace>(),
)
