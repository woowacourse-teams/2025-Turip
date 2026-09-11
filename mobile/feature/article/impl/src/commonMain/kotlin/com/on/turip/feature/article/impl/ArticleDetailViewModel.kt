package com.on.turip.feature.article.impl

import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ArticleRepository
import com.on.turip.core.model.article.ArticleDetail
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.ui.BaseViewModel
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.feature.article.impl.model.toUiModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val articleRepository: ArticleRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<ArticleDetailIntent, ArticleDetailState, ArticleDetailEffect>(
        ArticleDetailState(),
    ) {
    private var loadJob: Job? = null

    override fun onIntent(intent: ArticleDetailIntent) {
        when (intent) {
            ArticleDetailIntent.Retry -> {
                loadArticle()
            }

            is ArticleDetailIntent.ClickPlaceMap -> {
                emitEffect(ArticleDetailEffect.OpenMap(intent.place.mapUrl))
            }
        }
    }

    fun initArticle(articleId: Long) {
        if (currentState.articleId == articleId && currentState.isFetched) return

        updateState { ArticleDetailState(articleId = articleId) }
        loadArticle()
    }

    private fun loadArticle() {
        val articleId: Long = currentState.articleId

        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                updateState { copy(isLoading = true, errorUiState = ErrorUiState.None) }

                when (val result = articleRepository.loadArticleDetail(articleId)) {
                    is TuripResult.Success -> {
                        handleLoadSuccess(result.value)
                    }

                    is TuripResult.Failure -> {
                        Napier.e("아티클 상세 조회 실패: $articleId", result.cause)
                        handleLoadFailure(result)
                    }
                }
            }
    }

    private fun handleLoadSuccess(articleDetail: ArticleDetail) {
        updateState {
            copy(
                article = articleDetail.toUiModel(),
                isLoading = false,
                isFetched = true,
            )
        }
        Napier.d("아티클 상세 조회: ${articleDetail.title}")
    }

    private fun handleLoadFailure(failure: TuripResult.Failure) {
        val errorUiState: ErrorUiState =
            when (failure.errorType.toUiError()) {
                UiError.Global.Network -> {
                    ErrorUiState.Network
                }

                UiError.Global.Server -> {
                    ErrorUiState.Server
                }

                UiError.Global.TokenExpired -> {
                    navigateToLogin()
                    return
                }

                UiError.Feature.NotFound -> {
                    ErrorUiState.NotFound
                }

                else -> {
                    ErrorUiState.Unexpected
                }
            }

        updateState {
            copy(
                isLoading = false,
                isFetched = true,
                errorUiState = errorUiState,
            )
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            sessionManager.switchToGuest()
            emitEffect(ArticleDetailEffect.NavigateToLogin)
        }
    }
}
