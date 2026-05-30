package com.on.turip.core.network.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripCreationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("accountId")
    val accountId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("isDefault")
    val isDefault: Boolean,
)
