package com.on.turip.feature.regionbriefing.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.randomtravel.api.RelatedSpotDetailNavKey
import com.on.turip.feature.regionbriefing.api.RegionBriefingNavKey
import com.on.turip.feature.regionbriefing.impl.RegionBriefingScreen
import com.on.turip.feature.trip.api.TripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class RegionBriefingNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(RegionBriefingNavKey::class, RegionBriefingNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<RegionBriefingNavKey> { key ->
            RegionBriefingScreen(
                regionCategoryName = key.regionCategoryName,
                visitorCount = key.visitorCount,
                baseMonth = key.baseMonth,
                onBackClick = navigator::goBack,
                onContentClick = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
                // 연관 관광지 상세는 랜덤 여행이 이미 갖고 있는 화면을 그대로 쓴다.
                onRelatedSpotClick = { regionCategoryName, spotCategory ->
                    navigator.navigate(
                        RelatedSpotDetailNavKey(
                            regionCategoryName = regionCategoryName,
                            spotCategory = spotCategory,
                        ),
                    )
                },
                onNavigateToLoginScreen = { navigator.goWithAllClear(LoginNavKey()) },
            )
        }
    }
}
