package com.on.turip.core.network.error

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val tag: String,
    val message: String,
)
