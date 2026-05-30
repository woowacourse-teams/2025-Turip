package com.on.turip.core.network.dto.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionCategoriesResponse(
    @SerialName("regionCategories")
    val regionCategories: List<RegionCategoryResponse>,
)
