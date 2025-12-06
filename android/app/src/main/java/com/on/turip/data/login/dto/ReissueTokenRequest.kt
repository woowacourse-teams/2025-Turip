package com.on.turip.data.login.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenRequest(
    @SerialName("refreshToken")
    val refreshToken: String,
)
