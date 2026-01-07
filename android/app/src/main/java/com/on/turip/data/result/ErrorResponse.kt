package com.on.turip.data.result

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("tag")
    val tag: String,
    @SerialName("message")
    val message: String,
)
