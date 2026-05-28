package com.on.turip.feature.search.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.SearchHistoryRepository
import com.on.turip.core.ui.BaseViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val MAX_SEARCH_HISTORY_COUNT = 10
private const val PAGE_SIZE = 100

class SearchViewModel(
    private val initialKeyword: String,
    private val contentRepository: ContentRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
) : BaseViewModel<SearchIntent, SearchState, SearchEffect>(SearchState(keyword = initialKeyword)) {

    init {
        loadHistory()
        if (initialKeyword.isNotBlank()) {
            searchByKeyword(initialKeyword)
        }
    }

    override fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateKeyword -> updateState { copy(keyword = intent.keyword) }
            SearchIntent.Search -> handleSearch()
            is SearchIntent.ClickHistory -> handleClickHistory(intent.keyword)
            is SearchIntent.DeleteHistory -> deleteHistory(intent.keyword)
            SearchIntent.ShowHistory -> updateState { copy(isHistoryVisible = true) }
            SearchIntent.HideHistory -> updateState { copy(isHistoryVisible = false) }
            SearchIntent.Retry -> handleSearch()
        }
    }

    private fun handleSearch() {
        val keyword = currentState.keyword
        if (keyword.isBlank()) return
        searchByKeyword(keyword)
        saveHistory(keyword)
        updateState { copy(isHistoryVisible = false) }
        emitEffect(SearchEffect.ClearFocus)
    }

    private fun handleClickHistory(keyword: String) {
        updateState { copy(keyword = keyword, isHistoryVisible = false) }
        searchByKeyword(keyword)
        saveHistory(keyword)
        emitEffect(SearchEffect.ClearFocus)
    }

    private fun loadHistory() {
        viewModelScope.launch {
            searchHistoryRepository.loadRecentSearches(MAX_SEARCH_HISTORY_COUNT)
                .onSuccess { histories ->
                    updateState { copy(searchHistory = histories.toImmutableList()) }
                }
        }
    }

    private fun saveHistory(keyword: String) {
        viewModelScope.launch {
            searchHistoryRepository.createSearchHistory(keyword)
                .onSuccess { loadHistory() }
        }
    }

    private fun deleteHistory(keyword: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearch(keyword)
                .onSuccess {
                    updateState {
                        copy(searchHistory = searchHistory.filter { it.keyword != keyword }.toImmutableList())
                    }
                }
        }
    }

    private fun searchByKeyword(keyword: String) {
        viewModelScope.launch {
            updateState { copy(resultState = SearchResultState.Loading) }

            val contentsDeferred = async {
                contentRepository.loadContentsByKeyword(keyword = keyword, size = PAGE_SIZE, lastId = 0L)
            }
            val countDeferred = async {
                contentRepository.loadContentsSizeByKeyword(keyword)
            }

            val contentsResult = contentsDeferred.await()
            val countResult = countDeferred.await()

            if (contentsResult.isFailure || countResult.isFailure) {
                updateState { copy(resultState = SearchResultState.Error) }
                return@launch
            }

            val contents = contentsResult.getOrThrow()
            val count = countResult.getOrThrow()

            updateState {
                copy(
                    resultState = if (count == 0) {
                        SearchResultState.Empty(keyword)
                    } else {
                        SearchResultState.Success(
                            contents = contents.toImmutableList(),
                            totalCount = count,
                        )
                    },
                )
            }
        }
    }
}
