package com.on.turip.ui.compose.search.regionresult.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.search.regionresult.RegionResultScreen
import com.on.turip.ui.compose.trip.navigation.TripDetailNavKey

fun EntryProviderScope<NavKey>.regionResultScreen(navigator: Navigator) {
    entry<RegionResultNavKey> {
        RegionResultScreen(
            regionCategoryName = it.regionCategoryName,
            onBackClick = navigator::goBack,
            onNavigateToTripDetail = { turipId: Long ->
                navigator.navigate(TripDetailNavKey(turipId))
            },
            onNavigateToLogin = { navigator.navigate(LoginNavKey) },
        )
    }
}
