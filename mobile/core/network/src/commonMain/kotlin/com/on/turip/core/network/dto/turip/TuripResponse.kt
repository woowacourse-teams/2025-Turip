package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class TuripResponse(
    val id: Long,
    val name: String,
    val isDefault: Boolean,
    val placeCount: Int,
    val memberCount: Int,
    val isShared: Boolean,
    val hasIncludePlace: Boolean,
)
