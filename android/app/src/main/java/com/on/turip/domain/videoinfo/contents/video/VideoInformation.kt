package com.on.turip.domain.videoinfo.contents.video

import com.on.turip.domain.videoinfo.contents.Content
import com.on.turip.domain.videoinfo.contents.video.trip.Trip

data class VideoInformation(
    val content: Content,
    val trip: Trip,
)
