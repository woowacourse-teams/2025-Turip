package com.on.turip.feature.randomtravel.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.randomtravel.api.RandomTravelNavKey
import com.on.turip.feature.randomtravel.api.RelatedSpotDetailNavKey
import com.on.turip.feature.randomtravel.impl.RandomTravelScreen
import com.on.turip.feature.randomtravel.impl.platform.rememberRelatedSpotDetailPlatformActions
import com.on.turip.feature.randomtravel.impl.relatedspot.RelatedSpotDetailScreen
import com.on.turip.feature.trip.api.TripDetailNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class RandomTravelNavKeyProvider : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(RandomTravelNavKey::class, RandomTravelNavKey.serializer())
        subclass(RelatedSpotDetailNavKey::class, RelatedSpotDetailNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<RandomTravelNavKey> {
            RandomTravelScreen(
                onBackClick = navigator::goBack,
                // 랜덤 여행은 새 도메인이 아니라 기존 Turip 생성 플로우로 들어가는 또 하나의 입구다.
                onContentClick = { contentId -> navigator.navigate(TripDetailNavKey(contentId)) },
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

        entry<RelatedSpotDetailNavKey> { key ->
            val platformActions = rememberRelatedSpotDetailPlatformActions()
            RelatedSpotDetailScreen(
                regionCategoryName = key.regionCategoryName,
                spotCategory = key.spotCategory,
                onBackClick = navigator::goBack,
                onNavigateToLoginScreen = { navigator.goWithAllClear(LoginNavKey()) },
                onOpenKakaoMap = platformActions.openKakaoMapUrl,
            )
        }
    }
}
