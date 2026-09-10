package com.on.turip.feature.popularregion.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.popularregion.api.PopularRegionNavKey
import com.on.turip.feature.popularregion.impl.PopularRegionScreen
import com.on.turip.feature.regionbriefing.api.RegionBriefingNavKey
import com.on.turip.feature.trip.api.TripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class PopularRegionNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(PopularRegionNavKey::class, PopularRegionNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<PopularRegionNavKey> {
            PopularRegionScreen(
                onBackClick = navigator::goBack,
                onRegionBriefingClick = { regionCategoryName, visitorCount, baseMonth ->
                    navigator.navigate(
                        RegionBriefingNavKey(
                            regionCategoryName = regionCategoryName,
                            visitorCount = visitorCount,
                            baseMonth = baseMonth,
                        ),
                    )
                },
                onContentClick = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
                onNavigateToLoginScreen = { navigator.goWithAllClear(LoginNavKey()) },
            )
        }
    }
}
