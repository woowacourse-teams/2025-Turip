package com.on.turip.core.network.dto.region

import kotlinx.serialization.Serializable

@Serializable
data class RegionCategoryResponse(
    val name: String,
    val imageUrl: String,
)
