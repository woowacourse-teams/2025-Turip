package com.on.turip.feature.invitation.impl

import androidx.compose.runtime.Immutable

@Immutable
sealed interface InvitationEntryDialogState {
    @Immutable
    data class Invalid(
        val target: InvalidInvitationTarget,
    ) : InvitationEntryDialogState

    @Immutable
    data class Failure(
        val target: InvalidInvitationTarget,
        val retryable: Boolean,
    ) : InvitationEntryDialogState
}
