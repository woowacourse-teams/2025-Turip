package com.on.turip.feature.regionbriefing.impl

import com.on.turip.core.ui.UiIntent

sealed interface RegionBriefingIntent : UiIntent {
    /** 영상 카드를 탭했다. 고르는 단계 없이 곧장 영상 상세로 간다. */
    data class ClickVideo(
        val contentId: Long,
    ) : RegionBriefingIntent

    data class ClickRelatedSpot(
        val spotCategory: String,
    ) : RegionBriefingIntent

    data object LoadMoreVideos : RegionBriefingIntent

    data object RetryVideos : RegionBriefingIntent

    data object RetryRelatedSpots : RegionBriefingIntent
}
