package com.on.turip.ui.search.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.contents.repository.DummyContentsRepository
import com.on.turip.domain.contents.PagedContentsResult
import com.on.turip.domain.contents.Video
import com.on.turip.domain.contents.repository.ContentsRepository
import kotlinx.coroutines.launch

class SearchResultViewModel(
    private val contentsRepository: ContentsRepository = DummyContentsRepository(),
) : ViewModel() {
    data class SearchResultState(
        val searchResultCount: Int = 3,
        val videos: List<Video> = emptyList(),
    )

    private val _searchResultState: MutableLiveData<SearchResultState> =
        MutableLiveData(SearchResultState())
    val searchResultState: LiveData<SearchResultState> get() = _searchResultState

    fun setRegionFromIntent(region: String) {
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
}
