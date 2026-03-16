package com.on.turip.ui.compose.invitation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import javax.inject.Inject

class InvitationEntryNavKeyProvider @Inject constructor() : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(InvitationEntryNavKey::class, InvitationEntryNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        invitationEntryScreen(navigator)
    }
}
