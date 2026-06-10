package com.on.turip.feature.trip.impl.model

import androidx.compose.runtime.Stable

@Stable
data class TripDetailInfoModel(
    val creatorName: String,
    val creatorThumbnail: String,
    val city: String,
    val videoLink: String,
    val contentTitle: String,
    val uploadedDate: String,
    val placeTotalCount: Int,
    val duration: TripDurationModel,
) {
    companion object {
        val Idle =
            TripDetailInfoModel(
                creatorName = "",
                creatorThumbnail = "",
                city = "",
                videoLink = "",
                contentTitle = "",
                uploadedDate = "",
                placeTotalCount = 0,
                duration = TripDurationModel(0, 0),
            )
    }
}
