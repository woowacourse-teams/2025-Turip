package com.on.turip.feature.trip.impl.platform

import androidx.compose.runtime.Composable
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.feature.trip.impl.model.MapModel

internal class TripDetailPlatformActions(
    val navigateToMap: (MapModel) -> Unit,
    val navigateToWebViewUrl: (String) -> Unit,
    val shareTuripByText: (TuripShareModel) -> Unit,
    val shareTuripInvitationLink: (String) -> Unit,
)

@Composable
internal expect fun rememberTripDetailPlatformActions(): TripDetailPlatformActions
