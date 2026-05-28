package com.on.turip.feature.search.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.search.api.RegionResultNavKey
import com.on.turip.feature.search.api.SearchNavKey
import com.on.turip.feature.search.impl.RegionResultScreen
import com.on.turip.feature.search.impl.SearchScreen
import com.on.turip.feature.trip.api.TripDetailNavKey

fun EntryProviderScope<NavKey>.searchScreens(navigator: Navigator) {
    entry<SearchNavKey> { key ->
        SearchScreen(
            keyword = key.keyword,
            onNavigateBack = { navigator.goBack() },
            onNavigateToDetail = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
            onNavigateToLogin = { navigator.navigate(LoginNavKey(deepLinkUrl = null)) },
        )
    }

    entry<RegionResultNavKey> { key ->
        RegionResultScreen(
            regionCategoryName = key.regionCategoryName,
            onBackClick = { navigator.goBack() },
            onNavigateToDetail = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
        )
    }
}
