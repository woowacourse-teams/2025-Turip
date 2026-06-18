package com.on.turip.feature.search.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.search.api.RegionResultNavKey
import com.on.turip.feature.search.api.SearchNavKey
import com.on.turip.feature.search.impl.keyword.SearchScreen
import com.on.turip.feature.search.impl.regionresult.RegionResultScreen
import com.on.turip.feature.trip.api.TripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class SearchNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(SearchNavKey::class, SearchNavKey.serializer())
        subclass(RegionResultNavKey::class, RegionResultNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<SearchNavKey> { key ->
            SearchScreen(
                keyword = key.keyword,
                onNavigateBack = navigator::goBack,
                onNavigateToDetail = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
                onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
            )
        }

        entry<RegionResultNavKey> { key ->
            RegionResultScreen(
                regionCategoryName = key.regionCategoryName,
                onBackClick = navigator::goBack,
                onNavigateToTripDetail = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
                onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
            )
        }
    }
}
