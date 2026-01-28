package com.on.turip.data.turip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripsResponse(
    @SerialName("turips")
    val turipsResponse: List<TuripPostResponse>,
)
