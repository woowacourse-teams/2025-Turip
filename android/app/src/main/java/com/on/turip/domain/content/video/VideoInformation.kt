package com.on.turip.domain.content.video

import com.on.turip.domain.content.Content
import com.on.turip.domain.trip.Trip

data class VideoInformation(
    val content: Content,
    val trip: Trip,
)
