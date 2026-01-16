package com.on.turip.ui.trip.detail

import androidx.compose.runtime.Stable
import com.on.turip.ui.common.model.trip.TripDurationModel

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
