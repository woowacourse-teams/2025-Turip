package com.on.turip.core.data.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripInvitationInformationResponse(
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("alreadyJoined")
    val alreadyJoined: Boolean,
)
