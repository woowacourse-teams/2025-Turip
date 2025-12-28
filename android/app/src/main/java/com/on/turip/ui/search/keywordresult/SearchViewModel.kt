package com.on.turip.ui.search.keywordresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.domain.searchhistory.SearchHistoryRepository
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.search.keywordresult.SearchActivity.Companion.SEARCH_KEYWORD_KEY
import com.on.turip.ui.search.model.VideoInformationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchingWord: MutableLiveData<String> = MutableLiveData()
    val searchingWord: LiveData<String> get() = _searchingWord

    private val _searchHistory: MutableLiveData<List<SearchHistory>> = MutableLiveData(emptyList())
    val searchHistory: LiveData<List<SearchHistory>> get() = _searchHistory

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    private val searchKeyword: String by lazy {
        checkNotNull(savedStateHandle[SEARCH_KEYWORD_KEY]) {
            Timber.e("검색 완료 화면 검색 결과가 존재하지 않습니다.")
        }
    }

    init {
        _searchingWord.value = searchKeyword
        loadSearchHistory()
        loadByKeyword(searchKeyword)
        createSearchHistory(searchKeyword)
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository
                .loadRecentSearches(MAX_SEARCH_HISTORY_COUNT)
                .onSuccess { result: List<SearchHistory> ->
                    Timber.d("최근 검색 목록 받아옴 $result")
                    _searchHistory.value = result
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    fun updateSearchingWord(newWord: String) {
        _searchingWord.value = newWord
    }

    fun loadByKeyword(searchingKeyword: String = searchingWord.value.orEmpty()) {
        if (searchingKeyword.isBlank()) return
        _loading.value = true
        viewModelScope.launch {
            val searchResultCountResult: Deferred<TuripCustomResult<Int>> =
                async {
                    contentRepository.loadContentsSizeByKeyword(
                        searchingKeyword,
                    )
                }

            val pagedContentsResult: Deferred<TuripCustomResult<PagedContentsResult>> =
                async {
                    contentRepository.loadContentsByKeyword(
                        keyword = searchingKeyword,
                        size = 100,
                        lastId = 0L,
                    )
                }

            pagedContentsResult
                .await()
                .onSuccess { result: PagedContentsResult ->
                    Timber.d("검색결과 목록들을 받아옴 $result")
                    val videoModels: List<VideoInformationModel> =
                        result.videos.map { videoInformation: VideoInformation -> videoInformation.toUiModel() }
                    _videoInformation.value = videoModels
                }.onFailure { errorEvent: ErrorEvent ->
                    _loading.value = false
                    checkError(errorEvent)
                }

            searchResultCountResult
                .await()
                .onSuccess { result: Int ->
                    Timber.d("최근 검색 목록 갯수를 받아옴 $result")
                    _loading.value = false
                    _searchResultCount.value = result
                    _serverError.value = false
                    _networkError.value = false
                }.onFailure { errorEvent: ErrorEvent ->
                    _loading.value = false
                    checkError(errorEvent)
                }
        }
    }

    private fun checkError(errorEvent: ErrorEvent) {
        when (errorEvent) {
            ErrorEvent.USER_NOT_HAVE_PERMISSION -> {
                _serverError.value = true
            }

            ErrorEvent.DUPLICATION_FOLDER -> {
                throw IllegalArgumentException("발생할 수 없는 오류")
            }

            ErrorEvent.UNEXPECTED_PROBLEM -> {
                _serverError.value = true
            }

            ErrorEvent.NETWORK_ERROR -> {
                _networkError.value = true
            }

            ErrorEvent.PARSER_ERROR -> {
                _serverError.value = true
            }

            ErrorEvent.TOKEN_EXPIRATION -> {
                viewModelScope.launch {
                    _uiEvent.send(CommonUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun createSearchHistory(searchingKeyword: String = searchingWord.value.orEmpty()) {
        if (searchingKeyword.isBlank()) return
        viewModelScope.launch {
            searchHistoryRepository
                .createSearchHistory(searchingKeyword)
                .onSuccess {
                    addSearchHistory(
                        SearchHistory(
                            keyword = searchingKeyword,
                            historyTime = System.currentTimeMillis(),
                        ),
                        MAX_SEARCH_HISTORY_COUNT,
                    )
                    Timber.d("최근 검색 목록에 추가됨")
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    fun addSearchHistory(
        newItem: SearchHistory,
        limit: Int,
    ) {
        val currentList = _searchHistory.value?.toMutableList()
        val updatedList = currentList?.filterNot { it.keyword == newItem.keyword }?.toMutableList()
        updatedList?.add(FIRST_INDEX, newItem)
        _searchHistory.value = updatedList?.take(limit)
    }

    fun deleteSearchHistory(keyword: String) {
        viewModelScope.launch {
            searchHistoryRepository
                .deleteSearch(keyword)
                .onSuccess {
                    _searchHistory.value = searchHistory.value?.filterNot { it.keyword == keyword }
                    Timber.d("${keyword}가 최근 검색 목록에서 삭제")
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    companion object {
        private const val MAX_SEARCH_HISTORY_COUNT = 10
        private const val FIRST_INDEX = 0
    }
}
