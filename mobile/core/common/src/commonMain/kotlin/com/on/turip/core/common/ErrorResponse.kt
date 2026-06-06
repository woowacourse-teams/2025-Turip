package com.on.turip.core.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("tag")
    val tag: String,
    @SerialName("message")
    val message: String,
)
