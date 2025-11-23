package com.on.turip.data.login.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginIdTokenPostRequest(
    @SerialName("idToken")
    val idToken: String,
)
