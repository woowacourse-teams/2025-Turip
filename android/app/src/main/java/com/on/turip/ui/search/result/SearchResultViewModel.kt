package com.on.turip.ui.search.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchResultViewModel(
    private val region: String,
    private val contentRepository: ContentRepository,
) : ViewModel() {
    private val _searchResultState: MutableLiveData<SearchResultState> =
        MutableLiveData(SearchResultState())
    val searchResultState: LiveData<SearchResultState> get() = _searchResultState

    init {
        loadContentsFromRegion()
        setTitle()
    }

    private fun loadContentsFromRegion() {
        viewModelScope.launch {
            val pagedContentsResult: Deferred<Result<PagedContentsResult>> =
                async {
                    contentRepository.loadContents(
                        region = region,
                        size = 100,
                        lastId = 0L,
                    )
                }
            val contentsSize: Deferred<Result<Int>> =
                async {
                    contentRepository.loadContentsSize(region)
                }

            pagedContentsResult
                .await()
                .onSuccess { result: PagedContentsResult ->
                    _searchResultState.value =
                        searchResultState.value?.copy(
                            videoInformations = result.videos,
                        )
                }.onFailure {
                }
            contentsSize
                .await()
                .onSuccess { result: Int ->
                    setSearchResultExistence(result)
                    updateLoading(false)
                }.onFailure {
                    updateLoading(false)
                }
        }
    }

    private fun updateLoading(state: Boolean) {
        _searchResultState.value =
            searchResultState.value?.copy(
                loading = state,
            )
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
            contentRepository: ContentRepository = RepositoryModule.contentRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SearchResultViewModel::class.java)) {
                        return SearchResultViewModel(
                            region,
                            contentRepository,
                        ) as T
                    }
                    throw IllegalArgumentException()
                }
            }
    }

    data class SearchResultState(
        val searchResultCount: Int = 0,
        val videoInformations: List<VideoInformation> = emptyList(),
        val region: String = "",
        val isExist: Boolean = false,
        val loading: Boolean = true,
    )
}
