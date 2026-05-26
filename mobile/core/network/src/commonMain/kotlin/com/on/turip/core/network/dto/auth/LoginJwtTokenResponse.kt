package com.on.turip.core.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginJwtTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
