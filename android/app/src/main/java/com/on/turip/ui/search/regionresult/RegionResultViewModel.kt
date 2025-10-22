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
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.search.model.VideoInformationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegionResultViewModel @Inject constructor(
    private val regionCategoryName: String,
    private val contentRepository: ContentRepository,
) : ViewModel() {
    private val _searchResultState: MutableLiveData<SearchResultState> =
        MutableLiveData(SearchResultState())
    val searchResultState: LiveData<SearchResultState> get() = _searchResultState

    private val _networkError: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: LiveData<Boolean> get() = _networkError

    private val _serverError: MutableLiveData<Boolean> = MutableLiveData(false)
    val serverError: LiveData<Boolean> get() = _serverError

    init {
        loadContentsFromRegion()
        setTitle()
    }

    fun reload() {
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
                    _networkError.value = false
                    _serverError.value = false
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                }
            contentsSize
                .await()
                .onSuccess { result: Int ->
                    setSearchResultExistence(result)
                    updateLoading(false)
                    _networkError.value = false
                    _serverError.value = false
                }.onFailure { errorEvent: ErrorEvent ->
                    updateLoading(false)
                    checkError(errorEvent)
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

    data class SearchResultState(
        val searchResultCount: Int = 0,
        val videoInformations: List<VideoInformationModel> = emptyList(),
        val region: String = "",
        val isExist: Boolean = false,
        val loading: Boolean = true,
    )
}
