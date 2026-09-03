package com.on.turip.feature.randomtravel.impl.platform

import androidx.compose.runtime.Composable

internal class RelatedSpotDetailPlatformActions(
    val openKakaoMapUrl: (String) -> Unit,
)

@Composable
internal expect fun rememberRelatedSpotDetailPlatformActions(): RelatedSpotDetailPlatformActions
