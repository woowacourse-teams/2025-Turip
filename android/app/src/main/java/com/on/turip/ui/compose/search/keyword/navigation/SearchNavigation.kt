package com.on.turip.ui.compose.search.keyword.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.search.keyword.SearchScreen
import com.on.turip.ui.compose.trip.navigation.TripDetailNavKey

fun EntryProviderScope<NavKey>.searchScreen(navigator: Navigator) {
    entry<SearchNavKey> {
        SearchScreen(
            keyword = it.keyword,
            onNavigateBack = navigator::goBack,
            onNavigateToDetail = { turipId: Long ->
                navigator.navigate(TripDetailNavKey(turipId))
            },
            onNavigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
        )
    }
}
