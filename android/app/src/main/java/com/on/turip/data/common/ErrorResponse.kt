package com.on.turip.data.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * data layer
 */
@Serializable
data class ErrorResponse(
    @SerialName("tag")
    val tag: String,
    @SerialName("message")
    val message: String,
)
