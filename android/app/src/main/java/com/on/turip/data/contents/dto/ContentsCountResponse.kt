package com.on.turip.data.contents.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentsCountResponse(
    @SerialName("count")
    val count: Int,
)
