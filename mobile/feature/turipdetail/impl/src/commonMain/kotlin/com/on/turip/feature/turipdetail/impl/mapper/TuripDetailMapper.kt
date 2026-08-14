package com.on.turip.feature.turipdetail.impl.mapper

import com.on.turip.core.model.bookmark.TuripPlace
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripType
import com.on.turip.core.ui.model.AppleMap
import com.on.turip.core.ui.model.turip.TuripEditModel
import com.on.turip.core.ui.model.turip.TuripPlaceShareModel
import com.on.turip.feature.turipdetail.impl.model.MapModel
import com.on.turip.feature.turipdetail.impl.model.MyTuripModel
import com.on.turip.feature.turipdetail.impl.model.TuripPlaceModel
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel

fun TuripPlace.toUiModel(): TuripPlaceModel =
    TuripPlaceModel(
        turipPlaceId = id,
        placeId = place.placeId,
        order = order,
        name = place.name,
        isTuripPlace = true,
        latitude = place.latitude,
        longitude = place.longitude,
        category = place.category.joinToString(),
        mapModel =
            MapModel.from(
                url = place.url,
                appleMap = AppleMap(place.name, place.latitude, place.longitude),
            ),
    )

fun TuripPlaceModel.toPlaceLatLngUiModel(): PlaceLatLngUiModel =
    PlaceLatLngUiModel(
        placeId = placeId,
        name = name,
        latitude = latitude,
        longitude = longitude,
    )

fun Turip.toUiMyTuripModel(): MyTuripModel =
    MyTuripModel(
        id = id,
        name = name,
        placeCount = placeCount,
        memberCount = memberCount,
        type = TuripType.from(isShared),
        isDefault = isDefault,
    )

fun MyTuripModel.toEditModel(): TuripEditModel =
    TuripEditModel(
        id = id,
        name = name,
        count = placeCount,
    )

fun TuripPlaceModel.toShareModel(): TuripPlaceShareModel =
    TuripPlaceShareModel(
        name = name,
        uri = mapModel.uri,
    )
