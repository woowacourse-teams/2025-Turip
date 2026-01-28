package com.on.turip.data.turip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripCreationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("isDefault")
    val isDefault: Boolean,
)
