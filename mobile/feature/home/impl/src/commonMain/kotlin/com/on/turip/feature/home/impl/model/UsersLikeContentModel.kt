package com.on.turip.feature.home.impl.model

import com.on.turip.core.model.content.Content
import com.on.turip.core.model.trip.TripDuration

data class UsersLikeContentModel(
    val content: Content,
    val tripDuration: TripDuration,
)
