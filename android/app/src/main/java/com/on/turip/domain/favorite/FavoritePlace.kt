package com.on.turip.domain.favorite

import com.on.turip.domain.trip.Place

data class FavoritePlace(
    val id: Long,
    val place: Place,
    val order: Long,
)
