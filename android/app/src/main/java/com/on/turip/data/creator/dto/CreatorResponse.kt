package com.on.turip.data.creator.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatorResponse(
    @SerialName("channelName")
    val channelName: String,
    @SerialName("id")
    val id: Long,
    @SerialName("profileImage")
    val profileImage: String,
)
