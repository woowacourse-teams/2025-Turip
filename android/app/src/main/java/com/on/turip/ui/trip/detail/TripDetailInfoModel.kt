package com.on.turip.ui.trip.detail

import com.on.turip.ui.common.model.trip.TripDurationModel

data class TripDetailInfoModel(
    val creatorName: String,
    val creatorThumbnail: String,
    val videoLink: String,
    val contentTitle: String,
    val uploadedDate: String,
    val placeTotalCount: Int,
    val duration: TripDurationModel,
)
