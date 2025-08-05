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
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.search.model.VideoInformationModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchViewModel(
    private val contentRepository: ContentRepository,
) : ViewModel() {
    private val _searchingWord: MutableLiveData<String> = MutableLiveData()
    val searchingWord: LiveData<String> = _searchingWord

    private val _videoInformation: MutableLiveData<List<VideoInformationModel>> = MutableLiveData()
    val videoInformation: LiveData<List<VideoInformationModel>> = _videoInformation

    private val _searchResultCount: MutableLiveData<Int> = MutableLiveData()
    val searchResultCount: LiveData<Int> = _searchResultCount

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading

    fun updateSearchingWord(newWords: Editable?) {
        _searchingWord.value = newWords.toString()
    }

    fun updateByKeyword() {
        viewModelScope.launch {
            val searchResultCountResult =
                async {
                    _loading.value = true
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

            searchResultCountResult
                .await()
                .onSuccess { result: Int ->
                    _searchResultCount.value = result
                }.onFailure {
                    // TODO 로그
                }

            pagedContentsResult
                .await()
                .onSuccess { result: PagedContentsResult ->
                    val videoModels: List<VideoInformationModel> =
                        result.videos.map { videoInformation: VideoInformation -> videoInformation.toUiModel() }
                    _videoInformation.value = videoModels
                    _loading.value = false
                }.onFailure {
                    // TODO 실패 했을 때
                }
        }
    }

    companion object {
        fun provideFactory(contentRepository: ContentRepository = RepositoryModule.contentRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchViewModel(
                        contentRepository,
                    )
                }
            }
    }
}
