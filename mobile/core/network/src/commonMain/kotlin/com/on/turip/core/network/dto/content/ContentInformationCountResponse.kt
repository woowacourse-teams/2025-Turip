package com.on.turip.core.network.dto.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentInformationCountResponse(
    @SerialName("count")
    val count: Int,
)
