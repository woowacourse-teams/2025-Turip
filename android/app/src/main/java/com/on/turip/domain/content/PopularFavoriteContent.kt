package com.on.turip.domain.content

import com.on.turip.domain.trip.Trip

data class PopularFavoriteContent(
    val content: Content,
    val isFavorite: Boolean,
    val city: String,
    val trip: Trip,
)
