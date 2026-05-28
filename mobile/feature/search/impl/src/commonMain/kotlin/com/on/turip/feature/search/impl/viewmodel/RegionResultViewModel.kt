package com.on.turip.feature.search.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.ui.BaseViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 100

class RegionResultViewModel(
    private val regionCategoryName: String,
    private val contentRepository: ContentRepository,
) : BaseViewModel<RegionResultIntent, RegionResultState, RegionResultEffect>(
    RegionResultState(regionName = regionCategoryName),
) {

    init {
        loadContents()
    }

    override fun onIntent(intent: RegionResultIntent) {
        when (intent) {
            RegionResultIntent.Retry -> loadContents()
        }
    }

    private fun loadContents() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isError = false, isEmpty = false) }

            val contentsDeferred = async {
                contentRepository.loadContentsByRegion(
                    regionCategory = regionCategoryName,
                    size = PAGE_SIZE,
                    lastId = 0L,
                )
            }
            val countDeferred = async {
                contentRepository.loadContentsSizeByRegion(regionCategoryName)
            }

            val contentsResult = contentsDeferred.await()
            val countResult = countDeferred.await()

            if (contentsResult.isFailure || countResult.isFailure) {
                updateState { copy(isLoading = false, isError = true) }
                return@launch
            }

            val contents = contentsResult.getOrThrow()
            val count = countResult.getOrThrow()

            updateState {
                copy(
                    isLoading = false,
                    contents = contents.toImmutableList(),
                    totalCount = count,
                    isEmpty = count == 0,
                )
            }
        }
    }
}
