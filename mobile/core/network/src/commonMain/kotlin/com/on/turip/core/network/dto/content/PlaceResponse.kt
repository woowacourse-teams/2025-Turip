package com.on.turip.core.network.dto.content

import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(
    val id: Long,
    val name: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
)
