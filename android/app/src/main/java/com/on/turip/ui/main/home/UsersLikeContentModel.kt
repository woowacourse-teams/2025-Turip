package com.on.turip.ui.main.home

import com.on.turip.domain.content.Content
import com.on.turip.ui.common.model.trip.TripDurationModel

data class UsersLikeContentModel(
    val content: Content,
    val tripDuration: TripDurationModel,
)
