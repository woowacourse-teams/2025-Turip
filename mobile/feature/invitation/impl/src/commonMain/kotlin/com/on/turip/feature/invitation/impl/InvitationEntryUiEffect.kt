package com.on.turip.feature.invitation.impl

sealed interface InvitationEntryUiEffect {
    data object NavigateToHome : InvitationEntryUiEffect

    data class NavigateToTuripDetail(
        val turipId: Long,
    ) : InvitationEntryUiEffect

    data object NavigateToLogin : InvitationEntryUiEffect
}
