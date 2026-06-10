package com.on.turip.feature.trip.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.trip.api.TripDetailNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.trip.impl.TripDetailScreen
import com.on.turip.feature.turipdetail.api.TuripDetailNavKey
import com.on.turip.feature.trip.impl.platform.rememberTripDetailPlatformActions
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class TripNavKeyProvider(
    private val webViewBaseUrl: String,
) : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(TripDetailNavKey::class, TripDetailNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        entry<TripDetailNavKey> { key ->
            val platformActions = rememberTripDetailPlatformActions()
            TripDetailScreen(
                contentId = key.contentId,
                navigateToBack = navigator::goBack,
                navigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
                navigateToTuripDetail = { turipId -> navigator.navigate(TuripDetailNavKey(turipId)) },
                navigateToMap = platformActions.navigateToMap,
                navigateToWebViewUrl = platformActions.navigateToWebViewUrl,
                navigateToShareTuripByText = platformActions.shareTuripByText,
                navigateToShareTuripInvitationLink = platformActions.shareTuripInvitationLink,
                webViewBaseUrl = webViewBaseUrl,
            )
        }
    }
}
