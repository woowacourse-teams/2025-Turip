package com.on.turip.feature.turipdetail.impl.platform

import androidx.compose.runtime.Composable
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.feature.turipdetail.impl.model.MapModel

internal class TuripDetailPlatformActions(
    val navigateToMap: (MapModel) -> Unit,
    val shareTuripByText: (TuripShareModel) -> Unit,
    val shareTuripInvitationLink: (String) -> Unit,
)

@Composable
internal expect fun rememberTuripDetailPlatformActions(): TuripDetailPlatformActions
