package com.on.turip.ui.compose.invitation

sealed interface InvitationEntryUiEffect {
    data object NavigateToHome : InvitationEntryUiEffect

    data class NavigateToTuripDetail(
        val turipId: Long,
    ) : InvitationEntryUiEffect

    data object NavigateToLogin : InvitationEntryUiEffect
}
