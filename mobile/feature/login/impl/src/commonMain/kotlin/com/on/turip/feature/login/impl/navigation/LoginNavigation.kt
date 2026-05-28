package com.on.turip.feature.login.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.invitation.api.InvitationEntryNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.login.impl.LoginScreen

fun EntryProviderScope<NavKey>.loginScreen(
    navigator: Navigator,
    deepLinkUrl: String?,
) {
    entry<LoginNavKey> { key ->
        LoginScreen(
            deepLinkUrl = key.deepLinkUrl,
            onNavigateToHome = { navigator.replace(HomeNavKey) },
            onNavigateToInvitationEntry = { url ->
                navigator.replace(InvitationEntryNavKey(url))
            },
        )
    }
}
