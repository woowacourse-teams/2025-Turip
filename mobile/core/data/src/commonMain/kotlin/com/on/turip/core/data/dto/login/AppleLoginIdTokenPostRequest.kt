package com.on.turip.core.data.dto.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppleLoginIdTokenPostRequest(
    @SerialName("idToken")
    val idToken: String,
    @SerialName("nonce")
    val nonce: String,
)
