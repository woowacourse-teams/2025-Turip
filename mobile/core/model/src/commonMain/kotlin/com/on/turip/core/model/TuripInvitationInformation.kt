package com.on.turip.core.model

data class TuripInvitationInformation(
    val turipId: Long,
    val turipName: String,
    val memberCount: Int,
    val isAlreadyJoined: Boolean,
)
