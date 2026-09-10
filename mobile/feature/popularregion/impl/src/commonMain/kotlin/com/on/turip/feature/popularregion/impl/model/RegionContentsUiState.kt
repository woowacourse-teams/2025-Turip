package com.on.turip.feature.popularregion.impl.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

/**
 * 시트 안 연관 콘텐츠 영역의 상태.
 *
 * 방문자 수는 전국 17개 시도 전부에 있지만 튜립 콘텐츠는 일부 지역에만 있다.
 * 콘텐츠가 없는 지역을 조용히 비워두면 로딩 실패와 구분되지 않으므로 사유를 상태로 남긴다.
 */
@Immutable
sealed interface RegionContentsUiState {
    data object Loading : RegionContentsUiState

    /** 아직 튜립이 다루지 않는 지역이라 조회할 카테고리 자체가 없다. */
    data object Unsupported : RegionContentsUiState

    data object Error : RegionContentsUiState

    data class Success(
        val contents: ImmutableList<RegionContentModel>,
    ) : RegionContentsUiState
}
