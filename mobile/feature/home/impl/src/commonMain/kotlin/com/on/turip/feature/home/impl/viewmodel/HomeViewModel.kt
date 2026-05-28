package com.on.turip.feature.home.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.domain.session.SessionManager
import com.on.turip.core.ui.BaseViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel(
    private val contentRepository: ContentRepository,
    private val regionRepository: RegionRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {

    init {
        loadContents()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadContents -> loadContents()
            is HomeIntent.SelectDomestic -> selectDomestic(intent.isDomestic)
        }
    }

    private fun loadContents() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isError = false) }

            val contentsDeferred = async { contentRepository.loadPopularFavoriteContents(size = 10) }
            val regionsDeferred = async { regionRepository.loadRegionCategories(currentState.isDomesticSelected) }

            val contentsResult = contentsDeferred.await()
            val regionsResult = regionsDeferred.await()

            if (contentsResult.isFailure || regionsResult.isFailure) {
                updateState { copy(isLoading = false, isError = true) }
                return@launch
            }

            updateState {
                copy(
                    isLoading = false,
                    usersLikeContents = contentsResult.getOrElse { emptyList() }.toImmutableList(),
                    regionCategories = regionsResult.getOrElse { emptyList() }.toImmutableList(),
                    isError = false,
                )
            }
        }
    }

    private fun selectDomestic(isDomestic: Boolean) {
        updateState { copy(isDomesticSelected = isDomestic) }
        viewModelScope.launch {
            regionRepository.loadRegionCategories(isDomestic)
                .onSuccess { regions ->
                    updateState { copy(regionCategories = regions.toImmutableList()) }
                }
        }
    }
}
