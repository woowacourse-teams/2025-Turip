package com.on.turip.feature.randomtravel.impl.relatedspot

import com.on.turip.core.ui.UiEffect

sealed interface RelatedSpotDetailEffect : UiEffect {
    data object NavigateToLogin : RelatedSpotDetailEffect
}
