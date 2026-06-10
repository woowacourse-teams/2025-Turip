package com.on.turip.feature.turipdetail.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.turipdetail.impl.TuripDetailScreen
import com.on.turip.feature.turipdetail.api.TuripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class TuripDetailNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(TuripDetailNavKey::class, TuripDetailNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<TuripDetailNavKey> { key ->
            TuripDetailScreen(
                selectedTuripId = key.turipId,
                onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
                onShareTuripByText = {},
                onShareTuripInvitationLink = {},
                onNavigateToMap = {},
                onBack = navigator::goBack,
            )
        }
    }
}
