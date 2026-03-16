package com.on.turip.ui.compose.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.home.HomeScreen
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.search.keyword.navigation.SearchNavKey
import com.on.turip.ui.compose.search.regionresult.navigation.RegionResultNavKey
import com.on.turip.ui.compose.trip.navigation.TripDetailNavKey

fun EntryProviderScope<NavKey>.homeScreen(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeScreen(
            onSearchClick = { keyword: String ->
                navigator.navigate(SearchNavKey(keyword))
            },
            onRegionClick = { regionCategoryName: String ->
                navigator.navigate(RegionResultNavKey(regionCategoryName))
            },
            onContentClick = { contentId: Long ->
                navigator.navigate(TripDetailNavKey(contentId))
            },
            onNavigateToLoginScreen = { navigator.goWithAllClear(LoginNavKey()) },
        )
    }
}
