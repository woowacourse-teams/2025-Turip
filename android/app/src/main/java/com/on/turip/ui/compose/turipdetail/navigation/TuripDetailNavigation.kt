package com.on.turip.ui.compose.turipdetail.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.login.navigation.LoginNavKey
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.turipdetail.TuripDetailScreen
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel

fun EntryProviderScope<NavKey>.turipDetailScreen(
    navigator: Navigator,
    navigateToMap: (mapModel: MapModel) -> Unit,
    onShareTurip: (turipShareModel: TuripShareModel) -> Unit,
) {
    entry<TuripDetailNavKey> {
        TuripDetailScreen(
            selectedTuripId = it.turipId,
            onNavigateToLogin = { navigator.navigate(LoginNavKey) },
            onShareTurip = onShareTurip,
            onNavigateToMap = navigateToMap,
            onBack = navigator::goBack,
        )
    }
}
