package com.on.turip.ui.common.mapper

import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.trip.ContentPlace
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.TripDuration
import com.on.turip.ui.common.model.trip.TripDurationModel
import com.on.turip.ui.common.model.trip.TripModel
import com.on.turip.ui.main.home.model.UsersLikeContentModel
import com.on.turip.ui.trip.detail.PlaceModel

fun Trip.toUiModel(): TripModel =
    TripModel(
        tripDurationModel = tripDuration.toUiModel(),
        tripPlaceCount = tripPlaceCount,
        contentPlaces = this@toUiModel.contentPlaces,
    )

fun Trip.toUiModelWithoutContentPlaces(): TripModel =
    TripModel(
        tripDurationModel = tripDuration.toUiModel(),
        tripPlaceCount = tripPlaceCount,
        contentPlaces = emptyList(),
    )

fun ContentPlace.toUiModel(): PlaceModel =
    PlaceModel(
        id = place.placeId,
        name = place.name,
        category = place.category.joinToString(),
        mapLink = place.url,
        timeLine = timeLine,
        isFavorite = isFavoritePlace,
    )

fun TripDuration.toUiModel(): TripDurationModel =
    TripDurationModel(
        nights = nights,
        days = days,
    )

fun UsersLikeContent.toUiModel(): UsersLikeContentModel =
    UsersLikeContentModel(
        content = content,
        tripDuration =
            TripDurationModel(
                nights = tripDuration.nights,
                days = tripDuration.days,
            ),
    )
