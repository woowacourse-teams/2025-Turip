package com.on.turip.core.data.dto.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionPopularityResponse(
    @SerialName("baseMonth")
    val baseMonth: String? = null,
    @SerialName("regions")
    val regions: List<RegionVisitorResponse> = emptyList(),
)
