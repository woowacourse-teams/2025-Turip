package com.on.turip.core.data.dto.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenRequest(
    @SerialName("refreshToken")
    val refreshToken: String,
)
