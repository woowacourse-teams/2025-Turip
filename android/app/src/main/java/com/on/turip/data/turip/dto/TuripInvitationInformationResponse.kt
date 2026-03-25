package com.on.turip.data.turip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripInvitationInformationResponse(
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("alreadyJoined")
    val alreadyJoined: Boolean,
)
