package com.on.turip.core.data.dto.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularDestinationResponse(
    @SerialName("baseMonth")
    val baseMonth: String? = null,
    @SerialName("destinations")
    val destinations: List<DestinationVisitorResponse> = emptyList(),
)

@Serializable
data class DestinationVisitorResponse(
    @SerialName("rank")
    val rank: Int,
    @SerialName("regionCategory")
    val regionCategory: String,
    @SerialName("visitorCount")
    val visitorCount: Long,
)
