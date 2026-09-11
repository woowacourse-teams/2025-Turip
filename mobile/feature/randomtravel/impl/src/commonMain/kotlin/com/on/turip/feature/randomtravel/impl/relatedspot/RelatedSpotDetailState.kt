package com.on.turip.feature.randomtravel.impl.relatedspot

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState
import com.on.turip.core.ui.error.ErrorUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class RelatedSpotDetailState(
    val regionCategoryName: String = "",
    val spotCategory: String = "",
    val spots: ImmutableList<String> = persistentListOf(),
    val isLoading: Boolean = true,
    val isFetched: Boolean = false,
    val errorUiState: ErrorUiState = ErrorUiState.None,
) : UiState {
    val shouldShowErrorScreen: Boolean = errorUiState != ErrorUiState.None

    /**
     * 카테고리는 목록 화면에서 넘어온 값이라 서버 응답에 없을 수 없지만,
     * 캐시가 갱신되는 사이 사라질 수 있어 빈 상태를 따로 그린다.
     */
    val shouldShowEmpty: Boolean = isFetched && spots.isEmpty() && errorUiState == ErrorUiState.None
}
