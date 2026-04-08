package com.on.turip.ui.common.mapper

import com.google.android.gms.maps.model.LatLng
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.turip.Turip
import com.on.turip.ui.common.model.turip.TuripModel
import com.on.turip.ui.common.model.turip.TuripPlaceShareModel
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turipdetail.model.turip.PlaceLatLngUiModel

fun TuripPlace.toUiModel(): TuripPlaceModel =
    TuripPlaceModel(
        turipPlaceId = id,
        placeId = place.placeId,
        order = order,
        name = place.name,
        isTuripPlace = true,
        latLng = LatLng(place.latitude, place.longitude),
        category = place.category.joinToString(),
        mapLink = place.url,
    )

fun TuripPlace.toLatLng(): PlaceLatLngUiModel =
    PlaceLatLngUiModel(
        placeId = place.placeId,
        name = place.name,
        latLng = LatLng(place.latitude, place.longitude),
    )

fun Turip.toUiModel(): TuripModel =
    TuripModel(
        id = id,
        name = name,
        placeCount = placeCount,
        isSelected = hasIncludePlace,
        isDefault = isDefault,
    )

fun TuripPlaceModel.toUiModel(): TuripPlaceShareModel =
    TuripPlaceShareModel(
        name = name,
        uri = mapModel.uri,
    )
