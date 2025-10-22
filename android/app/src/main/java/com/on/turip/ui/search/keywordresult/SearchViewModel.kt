package com.on.turip.ui.search.keywordresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.domain.searchhistory.SearchHistoryRepository
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.search.model.VideoInformationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    searchKeyword: String,
    private val contentRepository: ContentRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
) : ViewModel() {
    private val _searchingWord: MutableLiveData<String> = MutableLiveData()
    val searchingWord: LiveData<String> get() = _searchingWord

    private val _videoInformation: MutableLiveData<List<VideoInformationModel>> = MutableLiveData()
    val videoInformation: LiveData<List<VideoInformationModel>> get() = _videoInformation

    private val _searchResultCount: MutableLiveData<Int> = MutableLiveData()
    val searchResultCount: LiveData<Int> get() = _searchResultCount

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> get() = _loading

    private val _searchHistory: MutableLiveData<List<SearchHistory>> = MutableLiveData(emptyList())
    val searchHistory: LiveData<List<SearchHistory>> get() = _searchHistory

    private val _networkError: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: LiveData<Boolean> get() = _networkError

    private val _serverError: MutableLiveData<Boolean> = MutableLiveData(false)
    val serverError: LiveData<Boolean> get() = _serverError

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

            ErrorEvent.DUPLICATION_FOLDER -> throw IllegalArgumentException("발생할 수 없는 오류")
            ErrorEvent.UNEXPECTED_PROBLEM -> {
                _serverError.value = true
            }

            ErrorEvent.NETWORK_ERROR -> {
                _networkError.value = true
            }

            ErrorEvent.PARSER_ERROR -> {
                _serverError.value = true
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

        fun provideFactory(
            searchKeyword: String = "",
            contentRepository: ContentRepository = RepositoryModule.contentRepository,
            searchHistoryRepository: SearchHistoryRepository = RepositoryModule.searchHistoryRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchViewModel(
                        searchKeyword,
                        contentRepository,
                        searchHistoryRepository,
                    )
                }
            }
    }
}
