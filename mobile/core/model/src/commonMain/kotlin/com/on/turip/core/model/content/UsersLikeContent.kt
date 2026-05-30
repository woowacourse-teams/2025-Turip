package com.on.turip.core.model.content

import com.on.turip.core.model.trip.TripDuration

data class UsersLikeContent(
    val content: Content,
    val tripDuration: TripDuration,
)
