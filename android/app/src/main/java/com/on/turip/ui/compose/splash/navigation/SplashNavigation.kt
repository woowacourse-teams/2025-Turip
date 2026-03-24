package com.on.turip.ui.compose.splash.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.invitation.navigation.InvitationEntryNavKey
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.splash.SplashScreen

fun EntryProviderScope<NavKey>.splashScreen(
    navigator: Navigator,
    deepLinkUrl: String?,
) {
    entry<SplashNavKey> {
        SplashScreen(
            deepLinkUrl = deepLinkUrl,
            onNavigateToMain = { navigator.navigate(HomeNavKey) },
            onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey(deepLinkUrl)) },
            onNavigateToInvitationEntry = { navigator.navigate(InvitationEntryNavKey(it)) },
            onFinish = { navigator.goBack() },
        )
    }
}
