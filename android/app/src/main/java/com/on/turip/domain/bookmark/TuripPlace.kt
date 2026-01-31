package com.on.turip.domain.bookmark

import com.on.turip.domain.trip.Place

data class TuripPlace(
    val id: Long,
    val place: Place,
    val order: Long,
)
