package com.on.turip.feature.trip.impl.mapper

import com.on.turip.core.model.trip.ContentPlace
import com.on.turip.core.model.trip.TripDuration
import com.on.turip.feature.trip.impl.model.PlaceModel
import com.on.turip.feature.trip.impl.model.TripDurationModel

fun ContentPlace.toUiModel(): PlaceModel =
    PlaceModel(
        id = place.placeId,
        name = place.name,
        category = place.category.joinToString(),
        mapLink = place.url,
        timeLine = timeLine,
        isTuripPlace = isTuripPlace,
    )

fun TripDuration.toUiModel(): TripDurationModel =
    TripDurationModel(
        nights = nights,
        days = days,
    )
