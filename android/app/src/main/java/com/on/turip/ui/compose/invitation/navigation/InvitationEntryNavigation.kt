package com.on.turip.ui.compose.invitation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.invitation.InvitationEntryScreen
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.turip.navigation.MyTuripNavKey
import com.on.turip.ui.compose.turipdetail.navigation.TuripDetailNavKey

fun EntryProviderScope<NavKey>.invitationEntryScreen(navigator: Navigator) {
    entry<InvitationEntryNavKey> {
        InvitationEntryScreen(
            deepLinkUrl = it.deepLinkUrl,
            onNavigateToHome = { navigator.goWithAllClear(HomeNavKey) },
            onNavigateToTuripDetail = { turipId ->
                navigator.goWithAllClear(key = TuripDetailNavKey(turipId), parentTopLevelKey = MyTuripNavKey)
            },
            onNavigateToLogin = {
                navigator.goWithAllClear(LoginNavKey(deepLinkUrl = it.deepLinkUrl))
            },
        )
    }
}
