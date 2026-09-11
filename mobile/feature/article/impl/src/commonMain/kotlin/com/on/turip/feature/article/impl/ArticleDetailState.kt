package com.on.turip.feature.article.impl

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.article.impl.model.ArticleDetailUiModel

@Immutable
data class ArticleDetailState(
    val articleId: Long = 0L,
    val article: ArticleDetailUiModel? = null,
    val isLoading: Boolean = true,
    val isFetched: Boolean = false,
    val errorUiState: ErrorUiState = ErrorUiState.None,
) : UiState {
    val shouldShowErrorScreen: Boolean = errorUiState != ErrorUiState.None

    val shouldShowContent: Boolean = article != null && errorUiState == ErrorUiState.None
}
