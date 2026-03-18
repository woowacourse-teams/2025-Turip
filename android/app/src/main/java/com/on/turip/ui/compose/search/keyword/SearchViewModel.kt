package com.on.turip.ui.compose.search.keyword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.TuripResult
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.domain.searchhistory.SearchHistoryRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.compose.search.model.VideoInformationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<SearchUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<SearchUiEffect> = _uiEffect.receiveAsFlow()

    private val _searchingWord = MutableStateFlow("")
    val searchingWord: StateFlow<String> = _searchingWord.asStateFlow()

    private val _searchHistory = MutableStateFlow<ImmutableList<SearchHistory>>(persistentListOf())
    val searchHistory: StateFlow<ImmutableList<SearchHistory>> = _searchHistory.asStateFlow()

    fun initKeyword(keyword: String) {
        _searchingWord.update { keyword }
        loadSearchHistory()
        loadByKeyword(keyword)
        createSearchHistory(keyword)
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository
                .loadRecentSearches(MAX_SEARCH_HISTORY_COUNT)
                .onSuccess { result: List<SearchHistory> ->
                    Timber.d("최근 검색 목록 받아옴 $result")
                    _searchHistory.update { result.toImmutableList() }
                }
        }
    }

    fun updateSearchingWord(newWord: String) {
        _searchingWord.update { newWord }
    }

    fun loadByKeyword(searchingKeyword: String = searchingWord.value.orEmpty()) {
        if (searchingKeyword.isBlank()) return

        viewModelScope.launch {
            _uiState.update { SearchUiState.Loading }

            val searchResultCountDeferred: Deferred<TuripResult<Int>> =
                async { contentRepository.loadContentsSizeByKeyword(searchingKeyword) }

            val pagedContentsDeferred: Deferred<TuripResult<PagedContentsResult>> =
                async {
                    contentRepository.loadContentsByKeyword(
                        keyword = searchingKeyword,
                        size = 100,
                        lastId = 0L,
                    )
                }

            val countResult: TuripResult<Int> = searchResultCountDeferred.await()
            val contentsResult: TuripResult<PagedContentsResult> =
                pagedContentsDeferred.await()

            val failure: TuripResult.Failure? =
                listOf(countResult, contentsResult)
                    .filterIsInstance<TuripResult.Failure>()
                    .firstOrNull()

            if (failure != null) {
                handleError(failure)
                return@launch
            }

            val count: Int = (countResult as TuripResult.Success).value
            val videosInformation: List<VideoInformationModel> =
                (contentsResult as TuripResult.Success).value.videos.map { videoInformation: VideoInformation ->
                    videoInformation.toUiModel()
                }

            if (count == 0) {
                _uiState.update { SearchUiState.Empty(keyword = searchingKeyword) }
            } else {
                _uiState.update {
                    SearchUiState.Success(
                        videos = videosInformation.toImmutableList(),
                        totalCount = count,
                    )
                }
            }
            Timber.d("최근 검색 목록 갯수를 받아옴 $count")
            Timber.d("검색결과 목록들을 받아옴 $videosInformation")
        }
    }

    fun createSearchHistory(searchingKeyword: String = searchingWord.value.orEmpty()) {
        if (searchingKeyword.isBlank()) return
        viewModelScope.launch {
            searchHistoryRepository
                .createSearchHistory(searchingKeyword)
                .onSuccess {
                    addSearchHistory(
                        newItem =
                            SearchHistory(
                                keyword = searchingKeyword,
                                historyTime = System.currentTimeMillis(),
                            ),
                        limit = MAX_SEARCH_HISTORY_COUNT,
                    )
                    Timber.d("최근 검색 목록에 추가됨")
                }
        }
    }

    private fun addSearchHistory(
        newItem: SearchHistory,
        limit: Int,
    ) {
        _searchHistory.update { currentList ->
            val updatedList =
                currentList.filterNot { it.keyword == newItem.keyword }.toMutableList()
            updatedList.add(FIRST_INDEX, newItem)
            updatedList.take(limit).toImmutableList()
        }
    }

    fun deleteSearchHistory(keyword: String) {
        viewModelScope.launch {
            searchHistoryRepository
                .deleteSearch(keyword)
                .onSuccess {
                    _searchHistory.update {
                        it
                            .filterNot { it.keyword == keyword }
                            .toImmutableList()
                    }
                    Timber.d("${keyword}가 최근 검색 목록에서 삭제")
                }
        }
    }

    private suspend fun handleError(failure: TuripResult.Failure) {
        val uiError: UiError = failure.errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> _uiState.update { SearchUiState.Error(ErrorUiState.Network) }
                UiError.Global.Server -> _uiState.update { SearchUiState.Error(ErrorUiState.Server) }
                UiError.Global.TokenExpired -> _uiEffect.send(SearchUiEffect.NavigateToLogin)
            }
        }
    }

    companion object {
        private const val MAX_SEARCH_HISTORY_COUNT = 10
        private const val FIRST_INDEX = 0
    }
}
