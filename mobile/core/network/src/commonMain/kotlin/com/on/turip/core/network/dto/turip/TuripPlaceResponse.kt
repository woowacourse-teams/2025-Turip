package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class TuripPlaceResponse(
    val turipId: Long,
    val placeId: Long,
    val name: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val order: Int,
)
