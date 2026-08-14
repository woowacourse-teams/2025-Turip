package com.on.turip.core.model.bookmark

import com.on.turip.core.model.trip.Place

data class TuripPlace(
    val id: Long,
    val place: Place,
    val order: Long,
)
