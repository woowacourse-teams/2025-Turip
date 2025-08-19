package com.on.turip.domain.favorite

import com.on.turip.domain.content.Content
import com.on.turip.domain.trip.TripDuration

data class FavoriteContent(
    val content: Content,
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
)
