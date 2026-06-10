package com.on.turip.feature.invitation.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.invitation.api.InvitationEntryNavKey
import com.on.turip.feature.invitation.impl.InvitationEntryScreen
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.turipdetail.api.TuripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class InvitationNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(InvitationEntryNavKey::class, InvitationEntryNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<InvitationEntryNavKey> { key ->
            InvitationEntryScreen(
                deepLinkUrl = key.deepLinkUrl,
                onNavigateToHome = { navigator.goWithAllClear(HomeNavKey) },
                onNavigateToTuripDetail = { turipId -> navigator.goWithAllClear(TuripDetailNavKey(turipId)) },
                onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey(key.deepLinkUrl)) },
            )
        }
    }
}
