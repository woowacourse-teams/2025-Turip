package com.on.turip.ui.compose.search.keyword.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import javax.inject.Inject

class SearchNavKeyProvider @Inject constructor() : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(SearchNavKey::class, SearchNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        searchScreen(navigator)
    }
}
