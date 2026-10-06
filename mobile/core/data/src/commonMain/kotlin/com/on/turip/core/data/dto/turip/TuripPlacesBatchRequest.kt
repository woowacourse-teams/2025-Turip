package com.on.turip.core.data.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripPlacesBatchRequest(
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("placeIds")
    val placeIds: List<Long>,
)
