package com.on.turip.feature.regionbriefing.impl

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.model.content.VideoSummaryModel
import com.on.turip.core.ui.model.region.RelatedSpotsUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 지역 브리핑 화면의 상태.
 *
 * 지역명·방문자 수는 [com.on.turip.feature.regionbriefing.api.RegionBriefingNavKey] 로 받아 처음부터 채워져 있다.
 * 화면이 조회하는 것은 영상 목록과 연관 관광지 둘뿐이다.
 *
 * @param baseMonth 방문자 수 기준월(`yyyyMM`). 문구로 만드는 일은 화면이 한다.
 */
@Immutable
data class RegionBriefingState(
    val regionName: String = "",
    val visitorCountText: String = "",
    val baseMonth: String? = null,
    val videos: ImmutableList<VideoSummaryModel> = persistentListOf(),
    val relatedSpotsUiState: RelatedSpotsUiState = RelatedSpotsUiState.Loading,
    /** 진입하자마자 조회를 시작하므로 첫 프레임부터 스켈레톤을 보여준다. */
    val isVideoListLoading: Boolean = true,
    val isVideoListFetched: Boolean = false,
    val isVideoListLoadable: Boolean = false,
    val isLoadingMoreVideos: Boolean = false,
    val videoErrorUiState: ErrorUiState = ErrorUiState.None,
) : UiState {
    val videoCount: Int = videos.size

    val shouldShowEmptyVideos: Boolean =
        isVideoListFetched && videos.isEmpty() && videoErrorUiState == ErrorUiState.None
}
