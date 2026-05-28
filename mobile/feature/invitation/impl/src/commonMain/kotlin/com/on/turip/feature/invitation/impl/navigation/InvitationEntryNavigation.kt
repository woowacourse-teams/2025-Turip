package com.on.turip.feature.invitation.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.invitation.api.InvitationEntryNavKey
import com.on.turip.feature.invitation.impl.InvitationEntryScreen

fun EntryProviderScope<NavKey>.invitationEntryScreen(navigator: Navigator) {
    entry<InvitationEntryNavKey> { key ->
        InvitationEntryScreen(
            deepLinkUrl = key.deepLinkUrl,
            onNavigateToHome = { navigator.replace(HomeNavKey) },
            onNavigateBack = { navigator.goBack() },
        )
    }
}
