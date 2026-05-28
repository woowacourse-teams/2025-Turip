package com.on.turip.feature.invitation.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface InvitationEntryEffect : UiEffect {
    data object NavigateToHome : InvitationEntryEffect
    data object NavigateBack : InvitationEntryEffect
    data class ShowSnackbar(val message: String) : InvitationEntryEffect
}
