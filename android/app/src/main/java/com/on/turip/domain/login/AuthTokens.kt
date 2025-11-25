package com.on.turip.domain.login

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val isNewMember: Boolean,
)
