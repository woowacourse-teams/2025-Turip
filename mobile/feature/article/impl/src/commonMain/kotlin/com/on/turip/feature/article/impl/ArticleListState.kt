package com.on.turip.feature.article.impl

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.article.impl.model.ArticleUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * @param hasNext 다음 페이지가 더 있는지. 다음 요청의 커서는 [articles] 마지막 항목의 id 다.
 * @param isAppending 다음 페이지를 불러오는 중인지. 첫 페이지 로딩은 [isLoading] 으로 구분한다.
 * @param appendErrorUiState 다음 페이지 조회 실패. 이미 받은 목록은 유지한 채 리스트 끝에 재시도만 보여준다.
 */
@Immutable
data class ArticleListState(
    val articles: ImmutableList<ArticleUiModel> = persistentListOf(),
    val hasNext: Boolean = false,
    val isLoading: Boolean = true,
    val isFetched: Boolean = false,
    val isAppending: Boolean = false,
    val errorUiState: ErrorUiState = ErrorUiState.None,
    val appendErrorUiState: ErrorUiState = ErrorUiState.None,
) : UiState {
    val shouldShowErrorScreen: Boolean = errorUiState != ErrorUiState.None

    val shouldShowEmptyView: Boolean = isFetched && !shouldShowErrorScreen && articles.isEmpty()

    val canLoadMore: Boolean =
        hasNext && !isAppending && appendErrorUiState == ErrorUiState.None && articles.isNotEmpty()
}
