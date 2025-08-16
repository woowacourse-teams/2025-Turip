package com.on.turip.ui.search.regionresult

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
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.search.model.VideoInformationModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RegionResultViewModel(
    private val regionCategoryName: String,
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
            val pagedContentsResult: Deferred<TuripCustomResult<PagedContentsResult>> =
                async {
                    contentRepository.loadContentsByRegion(
                        regionCategoryName = regionCategoryName,
                        size = 100,
                        lastId = 0L,
                    )
                }
            val contentsSize: Deferred<TuripCustomResult<Int>> =
                async {
                    contentRepository.loadContentsSizeByRegion(regionCategoryName)
                }

            pagedContentsResult
                .await()
                .onSuccess { result: PagedContentsResult ->
                    val videoModels: List<VideoInformationModel> =
                        result.videos.map { videoInformation: VideoInformation -> videoInformation.toUiModel() }
                    _searchResultState.value =
                        searchResultState.value?.copy(
                            videoInformations = videoModels,
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
                region = regionCategoryName,
            )
    }

    companion object {
        fun provideFactory(
            regionCategoryName: String,
            contentRepository: ContentRepository = RepositoryModule.contentRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    RegionResultViewModel(
                        regionCategoryName,
                        contentRepository,
                    )
                }
            }
    }

    data class SearchResultState(
        val searchResultCount: Int = 0,
        val videoInformations: List<VideoInformationModel> = emptyList(),
        val region: String = "",
        val isExist: Boolean = false,
        val loading: Boolean = true,
    )
}
