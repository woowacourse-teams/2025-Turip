package com.on.turip.feature.randomtravel.impl.relatedspot

import com.on.turip.core.ui.UiIntent

sealed interface RelatedSpotDetailIntent : UiIntent {
    data object Retry : RelatedSpotDetailIntent
}
