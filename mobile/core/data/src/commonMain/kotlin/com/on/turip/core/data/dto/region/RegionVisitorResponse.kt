package com.on.turip.core.data.dto.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionVisitorResponse(
    @SerialName("areaCode")
    val areaCode: Int,
    @SerialName("regionName")
    val regionName: String,
    @SerialName("visitorCount")
    val visitorCount: Long,
)
