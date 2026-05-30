package com.on.turip.core.network.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceTuripsRequest(
    @SerialName("turipIds")
    val turipIds: List<Long>,
)
