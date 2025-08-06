package com.on.turip.ui.search.keywordresult

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.domain.searchhistory.SearchHistoryRepository
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.search.model.VideoInformationModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(
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

    fun updateSearchingWord(newWords: Editable?) {
        _searchingWord.value = newWords.toString()
    }

    fun loadByKeyword() {
        _loading.value = true
        viewModelScope.launch {
            val searchResultCountResult: Deferred<Result<Int>> =
                async {
                    contentRepository.loadContentsSizeByKeyword(
                        searchingWord.value.toString(),
                    )
                }

            val pagedContentsResult: Deferred<Result<PagedContentsResult>> =
                async {
                    contentRepository.loadContentsByKeyword(
                        keyword = searchingWord.value.toString(),
                        size = 100,
                        lastId = 0L,
                    )
                }

            pagedContentsResult
                .await()
                .onSuccess { result: PagedContentsResult ->
                    val videoModels: List<VideoInformationModel> =
                        result.videos.map { videoInformation: VideoInformation -> videoInformation.toUiModel() }
                    _videoInformation.value = videoModels
                }.onFailure {
                    _loading.value = false
                    Timber.e("${it.message}")
                }

            searchResultCountResult
                .await()
                .onSuccess { result: Int ->
                    _loading.value = false
                    _searchResultCount.value = result
                }.onFailure {
                    _loading.value = false
                    Timber.e("${it.message}")
                }
        }
    }

    companion object {
        fun provideFactory(
            contentRepository: ContentRepository = RepositoryModule.contentRepository,
            searchHistoryRepository: SearchHistoryRepository = RepositoryModule.searchHistoryRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchViewModel(
                        contentRepository,
                        searchHistoryRepository,
                    )
                }
            }
    }
}
