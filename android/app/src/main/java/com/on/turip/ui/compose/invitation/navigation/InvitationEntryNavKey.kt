package com.on.turip.ui.compose.invitation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class InvitationEntryNavKey(
    val deepLinkUrl: String,
) : NavKey
