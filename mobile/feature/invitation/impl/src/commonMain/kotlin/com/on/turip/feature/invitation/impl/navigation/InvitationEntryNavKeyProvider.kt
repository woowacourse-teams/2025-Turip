package com.on.turip.feature.invitation.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.invitation.api.InvitationEntryNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class InvitationEntryNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(InvitationEntryNavKey::class, InvitationEntryNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        invitationEntryScreen(navigator)
    }
}
