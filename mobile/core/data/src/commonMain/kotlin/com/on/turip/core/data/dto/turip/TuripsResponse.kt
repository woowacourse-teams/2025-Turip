package com.on.turip.core.data.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripsResponse(
    @SerialName("turips")
    val turipsResponse: List<TuripResponse>,
)
