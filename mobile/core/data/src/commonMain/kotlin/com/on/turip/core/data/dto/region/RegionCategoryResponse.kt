package com.on.turip.core.data.dto.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionCategoryResponse(
    @SerialName("country")
    val country: CountryResponse?,
    @SerialName("regionCategoryImageUrl")
    val regionCategoryImageUrl: String,
    @SerialName("regionCategoryName")
    val regionCategoryName: String,
)
