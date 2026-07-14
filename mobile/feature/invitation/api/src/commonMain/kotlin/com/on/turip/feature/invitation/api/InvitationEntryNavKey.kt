package com.on.turip.feature.invitation.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class InvitationEntryNavKey(
    val deepLinkUrl: String,
) : NavKey
