package com.on.turip.core.model.content.video

import com.on.turip.core.model.content.Content
import com.on.turip.core.model.trip.Trip

data class VideoInformation(
    val content: Content,
    val trip: Trip,
)
