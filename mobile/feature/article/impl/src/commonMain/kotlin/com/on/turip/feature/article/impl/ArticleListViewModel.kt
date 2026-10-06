package com.on.turip.feature.article.impl

import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ArticleRepository
import com.on.turip.core.model.article.ArticlesResult
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.ui.BaseViewModel
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.feature.article.impl.model.toUiModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArticleListViewModel(
    private val articleRepository: ArticleRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<ArticleListIntent, ArticleListState, ArticleListEffect>(
        ArticleListState(),
    ) {
    private var loadJob: Job? = null

    init {
        loadFirstPage()
    }

    override fun onIntent(intent: ArticleListIntent) {
        when (intent) {
            ArticleListIntent.Retry -> {
                if (currentState.shouldShowErrorScreen) loadFirstPage() else loadNextPage()
            }

            ArticleListIntent.LoadNextPage -> {
                loadNextPage()
            }
        }
    }

    private fun loadFirstPage() {
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                updateState { ArticleListState(isLoading = true) }

                when (val result = articleRepository.loadArticles()) {
                    is TuripResult.Success -> {
                        handleFirstPageSuccess(result.value)
                    }

                    is TuripResult.Failure -> {
                        Napier.e("아티클 목록 조회 실패", result.cause)
                        handleFailure(result) { errorUiState ->
                            copy(isLoading = false, isFetched = true, errorUiState = errorUiState)
                        }
                    }
                }
            }
    }

    private fun loadNextPage() {
        if (currentState.isAppending || !currentState.hasNext) return

        val lastId: Long = currentState.articles.lastOrNull()?.id ?: return

        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                updateState { copy(isAppending = true, appendErrorUiState = ErrorUiState.None) }

                when (val result = articleRepository.loadArticles(lastId = lastId)) {
                    is TuripResult.Success -> {
                        handleNextPageSuccess(result.value)
                    }

                    is TuripResult.Failure -> {
                        Napier.e("아티클 목록 추가 조회 실패: lastId=$lastId", result.cause)
                        handleFailure(result) { errorUiState ->
                            copy(isAppending = false, appendErrorUiState = errorUiState)
                        }
                    }
                }
            }
    }

    private fun handleFirstPageSuccess(result: ArticlesResult) {
        updateState {
            copy(
                articles = result.articles.map { it.toUiModel() }.toImmutableList(),
                hasNext = result.loadable,
                isLoading = false,
                isFetched = true,
            )
        }
        Napier.d("아티클 목록 조회: ${result.articles.size}건")
    }

    private fun handleNextPageSuccess(result: ArticlesResult) {
        updateState {
            copy(
                articles = (articles + result.articles.map { it.toUiModel() }).toImmutableList(),
                hasNext = result.loadable,
                isAppending = false,
            )
        }
        Napier.d("아티클 목록 추가 조회: ${result.articles.size}건")
    }

    private fun handleFailure(
        failure: TuripResult.Failure,
        reduce: ArticleListState.(ErrorUiState) -> ArticleListState,
    ) {
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

                else -> {
                    ErrorUiState.Unexpected
                }
            }

        updateState { reduce(errorUiState) }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            sessionManager.switchToGuest()
            emitEffect(ArticleListEffect.NavigateToLogin)
        }
    }
}
