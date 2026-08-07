package com.on.turip.core.data.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("accountId")
    val accountId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("isDefault")
    val isDefault: Boolean,
    @SerialName("placeCount")
    val placeCount: Int,
    @SerialName("memberCount")
    val memberCount: Int,
    @SerialName("isShared")
    val isShared: Boolean,
)
