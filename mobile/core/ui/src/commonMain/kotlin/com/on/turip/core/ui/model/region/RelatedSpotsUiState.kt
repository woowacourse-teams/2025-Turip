package com.on.turip.core.ui.model.region

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

/**
 * 연관 관광지 섹션의 상태.
 *
 * 서버가 일부 지역만 지원하고 조회도 실패할 수 있는데, 이때 섹션을 조용히 숨기면
 * `아직 구현되지 않은 것`과 구분할 수 없다. 그래서 사유를 상태로 구분해 항상 무언가를 그린다.
 */
@Immutable
sealed interface RelatedSpotsUiState {
    data object Loading : RelatedSpotsUiState

    data class Success(
        val relatedSpots: ImmutableList<RelatedSpotModel>,
    ) : RelatedSpotsUiState {
        val isEmpty: Boolean = relatedSpots.isEmpty()
    }

    /** 서버가 지원하지 않는 지역 (400 `REGION_CATEGORY_INVALID`) */
    data object Unsupported : RelatedSpotsUiState

    data object Error : RelatedSpotsUiState
}
