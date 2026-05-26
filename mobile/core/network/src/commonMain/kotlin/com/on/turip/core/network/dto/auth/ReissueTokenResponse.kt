package com.on.turip.core.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
