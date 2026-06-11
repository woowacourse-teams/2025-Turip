package com.on.turip.feature.search.impl.mapper

import com.on.turip.core.model.content.video.VideoInformation
import com.on.turip.core.model.trip.TripDuration
import com.on.turip.feature.search.impl.model.TripDurationModel
import com.on.turip.feature.search.impl.model.TripModel
import com.on.turip.feature.search.impl.model.VideoInformationModel

internal fun VideoInformation.toUiModel(): VideoInformationModel =
    VideoInformationModel(
        content = content,
        tripModel =
            TripModel(
                tripDurationModel = trip.tripDuration.toUiModel(),
                tripPlaceCount = trip.tripPlaceCount,
                contentPlaces = trip.contentPlaces,
            ),
    )

private fun TripDuration.toUiModel(): TripDurationModel =
    TripDurationModel(
        nights = nights,
        days = days,
    )
