package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class TuripStatusResponse(
    val turipId: Long,
    val turipName: String,
    val isIncluded: Boolean,
)
