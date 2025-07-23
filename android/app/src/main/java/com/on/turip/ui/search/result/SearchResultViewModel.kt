package com.on.turip.ui.search.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.on.turip.data.contents.repository.DummyContentsRepository
import com.on.turip.domain.contents.PagedContentsResult
import com.on.turip.domain.contents.Video
import com.on.turip.domain.contents.repository.ContentsRepository
import kotlinx.coroutines.launch

class SearchResultViewModel(
    private val region: String,
    private val contentsRepository: ContentsRepository,
) : ViewModel() {
    private val _searchResultState: MutableLiveData<SearchResultState> =
        MutableLiveData(SearchResultState())
    val searchResultState: LiveData<SearchResultState> get() = _searchResultState

    init {
        loadContentsFromRegion()
        loadContentsSize()
        setTitle()
    }

    private fun loadContentsFromRegion() {
        viewModelScope.launch {
            runCatching {
                contentsRepository.loadContents(
                    region = region,
                    size = 100,
                    lastId = 0L,
                )
            }.onSuccess { result: PagedContentsResult ->
                _searchResultState.value =
                    searchResultState.value?.copy(
                        videos = result.videos,
                    )
            }
        }
    }

    private fun loadContentsSize() {
        viewModelScope.launch {
            runCatching {
                contentsRepository.loadContentsSize(region)
            }.onSuccess { result: Int ->
                setSearchResultExistence(result)
            }
        }
    }

    private fun setSearchResultExistence(result: Int) {
        if (result > 0) {
            _searchResultState.value =
                searchResultState.value?.copy(
                    searchResultCount = result,
                    isExist = true,
                )
        } else {
            _searchResultState.value =
                searchResultState.value?.copy(
                    isExist = false,
                )
        }
    }

    private fun setTitle() {
        _searchResultState.value =
            searchResultState.value?.copy(
                region = region,
            )
    }

    companion object {
        fun provideFactory(
            region: String,
            contentsRepository: ContentsRepository = DummyContentsRepository(),
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SearchResultViewModel::class.java)) {
                        return SearchResultViewModel(
                            region,
                            contentsRepository,
                        ) as T
                    }
                    throw IllegalArgumentException()
                }
            }
    }

    data class SearchResultState(
        val searchResultCount: Int = 0,
        val videos: List<Video> = emptyList(),
        val region: String = "",
        val isExist: Boolean = false,
    )
}
