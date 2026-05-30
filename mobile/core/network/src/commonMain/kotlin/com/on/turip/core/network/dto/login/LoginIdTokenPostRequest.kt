package com.on.turip.core.network.dto.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginIdTokenPostRequest(
    @SerialName("idToken")
    val idToken: String,
)
