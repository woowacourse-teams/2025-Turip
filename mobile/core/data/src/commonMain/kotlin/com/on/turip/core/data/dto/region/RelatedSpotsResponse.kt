package com.on.turip.core.data.dto.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelatedSpotsResponse(
    @SerialName("relatedSpots")
    val relatedSpots: List<RelatedSpotResponse>,
)

@Serializable
data class RelatedSpotResponse(
    @SerialName("category")
    val category: String,
    @SerialName("spots")
    val spots: List<String>,
)
