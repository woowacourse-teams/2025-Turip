package com.on.turip.feature.login.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.invitation.api.InvitationEntryNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.login.impl.LoginScreen
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class LoginNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(LoginNavKey::class, LoginNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<LoginNavKey> { key ->
            LoginScreen(
                deepLinkUrl = key.deepLinkUrl,
                onNavigateToMain = { deepLinkUrl ->
                    if (deepLinkUrl != null) {
                        navigator.goWithAllClear(InvitationEntryNavKey(deepLinkUrl))
                    } else {
                        navigator.goWithAllClear(HomeNavKey)
                    }
                },
            )
        }
    }
}
