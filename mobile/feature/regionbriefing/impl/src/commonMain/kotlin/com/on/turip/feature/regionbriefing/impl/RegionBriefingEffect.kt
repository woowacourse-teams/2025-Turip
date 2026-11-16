package com.on.turip.feature.regionbriefing.impl

import com.on.turip.core.ui.UiEffect

sealed interface RegionBriefingEffect : UiEffect {
    data class NavigateToTripDetail(
        val contentId: Long,
    ) : RegionBriefingEffect

    data class NavigateToRelatedSpotDetail(
        val regionCategoryName: String,
        val spotCategory: String,
    ) : RegionBriefingEffect

    data object NavigateToLogin : RegionBriefingEffect
}
