package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class CreateTuripRequest(
    val name: String,
)
