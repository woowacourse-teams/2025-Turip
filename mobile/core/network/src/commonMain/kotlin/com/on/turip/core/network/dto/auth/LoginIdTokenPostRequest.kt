package com.on.turip.core.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginIdTokenPostRequest(
    val idToken: String,
    val clientId: String,
)
