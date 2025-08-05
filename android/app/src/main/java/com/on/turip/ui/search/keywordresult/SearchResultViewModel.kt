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

class SearchResultViewModel(
    private val contentRepository: ContentRepository,
) : ViewModel() {
    private val _searchingWord: MutableLiveData<String> = MutableLiveData()
    val searchingWord: LiveData<String> = _searchingWord

    private val _videoInformation: MutableLiveData<List<VideoInformationModel>> = MutableLiveData()
    val videoInformation: LiveData<List<VideoInformationModel>> = _videoInformation

    private val _searchResultCount: MutableLiveData<Int> = MutableLiveData()
    val searchResultCount: LiveData<Int> = _searchResultCount

    fun updateSearchingWord(newWords: Editable?) {
        _searchingWord.value = newWords.toString()
    }

    fun updateByKeyword() {
        viewModelScope.async {
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
                    // TODO 실패 했을 때
                }
        }
    }

    companion object {
        fun provideFactory(contentRepository: ContentRepository = RepositoryModule.contentRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchResultViewModel(
                        contentRepository,
                    )
                }
            }
    }
}
