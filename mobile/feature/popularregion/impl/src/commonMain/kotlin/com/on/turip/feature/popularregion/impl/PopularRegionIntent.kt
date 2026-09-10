package com.on.turip.feature.popularregion.impl

import com.on.turip.core.ui.UiIntent

sealed interface PopularRegionIntent : UiIntent {
    /** 지도에서 지역을 탭했다. 하단 시트가 이 지역으로 열린다. */
    data class SelectRegion(
        val regionCode: String,
    ) : PopularRegionIntent

    /** 빈 곳을 탭했거나 시트를 아래로 내렸다. */
    data object DismissSheet : PopularRegionIntent

    /** 선택한 지역의 연관 콘텐츠 목록으로 이동 */
    data object ClickRelatedContents : PopularRegionIntent

    data class ClickContent(
        val contentId: Long,
    ) : PopularRegionIntent

    /** 지도 전체를 다시 불러온다. */
    data object RetryLoad : PopularRegionIntent

    /** 선택한 지역의 콘텐츠만 다시 불러온다. */
    data object RetryContents : PopularRegionIntent
}
