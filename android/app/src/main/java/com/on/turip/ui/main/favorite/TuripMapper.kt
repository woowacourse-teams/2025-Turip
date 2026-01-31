package com.on.turip.ui.main.favorite

import androidx.core.net.toUri
import com.google.android.gms.maps.model.LatLng
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.folder.Turip
import com.on.turip.ui.main.favorite.model.PlaceLatLngUiModel
import com.on.turip.ui.main.favorite.model.TuripModel
import com.on.turip.ui.main.favorite.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.TuripPlaceShareModel

fun TuripPlace.toUiModel(): TuripPlaceModel =
    TuripPlaceModel(
        turipPlaceId = id,
        order = order,
        placeId = place.placeId,
        name = place.name,
        uri = place.url.toUri(),
        category = place.category.joinToString(),
        isTuripPlace = true,
        latLng = LatLng(place.latitude, place.longitude),
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
    )

fun TuripPlaceModel.toUiModel(): TuripPlaceShareModel =
    TuripPlaceShareModel(
        name = name,
        uri = uri,
    )
