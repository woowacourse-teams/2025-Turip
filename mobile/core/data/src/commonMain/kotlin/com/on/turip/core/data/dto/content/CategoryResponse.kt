package com.on.turip.core.data.dto.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    @SerialName("name")
    val name: String,
)
