package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class InvitationTokenResponse(
    val token: String,
)
