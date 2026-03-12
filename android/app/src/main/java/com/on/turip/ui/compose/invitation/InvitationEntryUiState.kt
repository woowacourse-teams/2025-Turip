package com.on.turip.ui.compose.invitation

import androidx.compose.runtime.Immutable

@Immutable
data class InvitationEntryUiState(
    val deepLinkUrl: String?,
    val invitationTuripId: Long?,
    val invitationTuripName: String?,
    val dialogState: InvitationEntryDialogState?,
) {
    companion object {
        val Idle =
            InvitationEntryUiState(
                deepLinkUrl = null,
                invitationTuripId = null,
                invitationTuripName = null,
                dialogState = null,
            )
    }
}
