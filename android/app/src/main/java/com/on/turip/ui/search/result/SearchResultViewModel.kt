package com.on.turip.ui.search.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.on.turip.data.contents.repository.DummyContentsRepository
import com.on.turip.domain.contents.repository.ContentsRepository
import com.on.turip.domain.region.Region
import com.on.turip.ui.common.model.SearchResultModel

class SearchResultViewModel(
    val contentsRepository: ContentsRepository = DummyContentsRepository(),
) : ViewModel() {
    data class SearchResultState(
        val loading: Boolean = true,
        val error: Boolean = false,
        val region: Region,
        val searchResultCount: Int,
        val searchResultModel: SearchResultModel,
    )

    private val _searchResultState: MutableLiveData<SearchResultState> = MutableLiveData()
    val searchResultState: LiveData<SearchResultState> = _searchResultState
}
