package com.on.turip.ui.compose.trip.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.common.model.turip.TuripShareModel
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.trip.TripDetailScreen
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.turipdetail.navigation.TuripDetailNavKey

fun EntryProviderScope<NavKey>.tripDetailScreen(
    navigator: Navigator,
    navigateToMap: (mapModel: MapModel) -> Unit,
    navigateToWebViewUrl: (url: String) -> Unit,
    navigateToShareTuripByText: (turipShareModel: TuripShareModel) -> Unit,
    navigateToShareTuripInvitationLink: (invitationLink: String) -> Unit,
) {
    entry<TripDetailNavKey> {
        TripDetailScreen(
            contentId = it.contentId,
            navigateToBack = navigator::goBack,
            navigateToLogin = { navigator.goWithAllClear(LoginNavKey()) },
            navigateToMap = navigateToMap,
            navigateToWebViewUrl = navigateToWebViewUrl,
            navigateToTuripDetail = { turipId: Long ->
                navigator.navigate(TuripDetailNavKey(turipId))
            },
            navigateToShareTuripByText = navigateToShareTuripByText,
            navigateToShareTuripInvitationLink = navigateToShareTuripInvitationLink,
        )
    }
}
