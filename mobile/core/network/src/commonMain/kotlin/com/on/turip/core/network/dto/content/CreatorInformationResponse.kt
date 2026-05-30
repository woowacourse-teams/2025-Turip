package com.on.turip.core.network.dto.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatorInformationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("channelName")
    val channelName: String,
    @SerialName("profileImage")
    val profileImage: String,
)
