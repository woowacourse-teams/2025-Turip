package com.on.turip.core.network.dto.turip

import kotlinx.serialization.Serializable

@Serializable
data class InvitationInformationResponse(
    val turipId: Long,
    val turipName: String,
    val memberCount: Int,
    val isAlreadyJoined: Boolean,
)
