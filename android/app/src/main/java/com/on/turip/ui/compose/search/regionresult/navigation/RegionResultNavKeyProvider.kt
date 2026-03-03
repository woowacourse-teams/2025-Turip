package com.on.turip.ui.compose.search.regionresult.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import jakarta.inject.Inject
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class RegionResultNavKeyProvider @Inject constructor() : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(RegionResultNavKey::class, RegionResultNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        regionResultScreen(navigator)
    }
}
