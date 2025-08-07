package com.on.turip.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.repository.RegionRepository
import com.on.turip.ui.common.mapper.toUiModel
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val regionRepository: RegionRepository,
    private val contentRepository: ContentRepository,
) : ViewModel() {
    private val _regionCategories: MutableLiveData<List<RegionCategory>> = MutableLiveData()
    val regionCategories: LiveData<List<RegionCategory>> get() = _regionCategories

    private val _isSelectedDomestic: MutableLiveData<Boolean> = MutableLiveData()
    val isSelectedDomestic: LiveData<Boolean> get() = _isSelectedDomestic

    private val _usersLikeContents: MutableLiveData<List<UsersLikeContentModel>> =
        MutableLiveData()
    val usersLikeContents: LiveData<List<UsersLikeContentModel>> get() = _usersLikeContents

    init {
        loadUsersLikeContents()
        loadRegionCategories(isDomestic = true)
    }

    private fun loadUsersLikeContents() {
        viewModelScope.launch {
            contentRepository
                .loadPopularFavoriteContents()
                .onSuccess { contents: List<UsersLikeContent> ->
                    _usersLikeContents.value = contents.map { it.toUiModel() }
                    Timber.d("인기 찜 목록: $contents")
                }.onFailure {
                    Timber.e("인기 찜 목록 실패: ${it.message}")
                }
        }
    }

    fun loadRegionCategories(isDomestic: Boolean) {
        viewModelScope.launch {
            regionRepository
                .loadRegionCategories(isDomestic)
                .onSuccess { regionCategories: List<RegionCategory> ->
                    _regionCategories.value = regionCategories
                    _isSelectedDomestic.value = isDomestic
                    Timber.d("지역 카테고리 조회: $regionCategories")
                }.onFailure {
                    Timber.e("지역 카테고리 조회 실패: ${it.message}")
                }
        }
    }

    companion object {
        fun provideFactory(
            regionRepository: RegionRepository = RepositoryModule.regionRepository,
            contentRepository: ContentRepository = RepositoryModule.contentRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    HomeViewModel(
                        regionRepository,
                        contentRepository,
                    )
                }
            }
    }
}
