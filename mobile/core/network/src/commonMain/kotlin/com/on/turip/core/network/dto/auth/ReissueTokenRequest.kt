package com.on.turip.core.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenRequest(
    val refreshToken: String,
)
