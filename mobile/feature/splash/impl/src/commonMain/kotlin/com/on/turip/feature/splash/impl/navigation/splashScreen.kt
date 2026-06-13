package com.on.turip.feature.splash.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.invitation.api.InvitationEntryNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.splash.api.SplashNavKey
import com.on.turip.feature.splash.impl.SplashScreen

fun EntryProviderScope<NavKey>.splashScreen(
    navigator: Navigator,
) {
    entry<SplashNavKey> { key ->
        SplashScreen(
            deepLinkUrl = key.deepLinkUrl,
            onNavigateToMain = { navigator.goWithAllClear(HomeNavKey) },
            onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey(key.deepLinkUrl)) },
            onNavigateToInvitationEntry = { navigator.goWithAllClear(InvitationEntryNavKey(it)) },
            onFinish = { navigator.goBack() },
        )
    }
}
