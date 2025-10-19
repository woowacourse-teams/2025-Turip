package com.on.turip.ui.trip.detail

import com.on.turip.ui.common.model.trip.TripDurationModel

data class TripDetailInfoModel(
    val uploadedDate: String,
    val placeCount: Int,
    val duration: TripDurationModel,
)
