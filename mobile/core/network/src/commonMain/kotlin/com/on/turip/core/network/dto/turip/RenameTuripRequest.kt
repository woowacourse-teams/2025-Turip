package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class RenameTuripRequest(
    val name: String,
)
