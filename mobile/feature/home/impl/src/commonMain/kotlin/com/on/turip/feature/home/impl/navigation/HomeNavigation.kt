package com.on.turip.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.home.impl.HomeScreen
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.search.api.RegionResultNavKey
import com.on.turip.feature.search.api.SearchNavKey
import com.on.turip.feature.trip.api.TripDetailNavKey

fun EntryProviderScope<NavKey>.homeScreen(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeScreen(
            onSearchClick = { keyword -> navigator.navigate(SearchNavKey(keyword)) },
            onRegionClick = { regionName -> navigator.navigate(RegionResultNavKey(regionName)) },
            onContentClick = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
            onNavigateToLogin = { navigator.navigate(LoginNavKey(deepLinkUrl = null)) },
        )
    }
}
