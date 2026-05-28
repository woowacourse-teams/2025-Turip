package com.on.turip.feature.invitation.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface InvitationEntryIntent : UiIntent {
    data object ClickJoin : InvitationEntryIntent
    data object ClickBack : InvitationEntryIntent
}
