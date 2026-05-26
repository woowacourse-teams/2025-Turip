package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class ReorderTuripPlacesRequest(
    val turipId: Long,
    val placeIds: List<Long>,
)
