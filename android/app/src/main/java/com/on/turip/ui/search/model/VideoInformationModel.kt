package com.on.turip.ui.search.model

import com.on.turip.domain.content.Content
import com.on.turip.ui.common.model.trip.TripModel

data class VideoInformationModel(
    val content: Content,
    val tripModel: TripModel,
)
