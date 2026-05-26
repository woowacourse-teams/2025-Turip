package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class AddTuripPlaceRequest(
    val turipId: Long,
    val placeId: Long,
)
