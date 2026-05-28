package com.on.turip.feature.invitation.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.TuripInvitationInformation
import com.on.turip.core.ui.UiState

@Immutable
data class InvitationEntryState(
    val isLoading: Boolean = false,
    val isFetched: Boolean = false,
    val isJoining: Boolean = false,
    val invitationInfo: TuripInvitationInformation? = null,
) : UiState {
    val shouldShowContent: Boolean = isFetched && invitationInfo != null
    val shouldShowError: Boolean = isFetched && invitationInfo == null
}
