package com.on.turip.ui.main.favorite

import androidx.core.net.toUri
import com.google.android.gms.maps.model.LatLng
import com.on.turip.domain.favorite.TuripPlace
import com.on.turip.domain.folder.Turip
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.TuripPlaceLatLngUiModel
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

fun TuripPlace.toLatLng(): TuripPlaceLatLngUiModel =
    TuripPlaceLatLngUiModel(
        placeId = place.placeId,
        name = place.name,
        favoriteLatLng = LatLng(place.latitude, place.longitude),
    )

fun Turip.toUiModel(): FavoritePlaceFolderModel =
    FavoritePlaceFolderModel(
        id = id,
        name = name,
        placeCount = placeCount,
        isSelected = isTuripPlace,
    )

fun TuripPlaceModel.toUiModel(): TuripPlaceShareModel =
    TuripPlaceShareModel(
        name = name,
        uri = uri,
    )
