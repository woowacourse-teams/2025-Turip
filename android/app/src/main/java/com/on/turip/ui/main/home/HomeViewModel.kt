package com.on.turip.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.ErrorEvent
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

    private val _networkError: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: LiveData<Boolean> get() = _networkError

    private val _serverError: MutableLiveData<Boolean> = MutableLiveData(false)
    val serverError: LiveData<Boolean> get() = _serverError

    init {
        loadUsersLikeContents()
        loadRegionCategories(isDomestic = true)
    }

    fun reload() {
        loadUsersLikeContents()
        loadRegionCategories(isDomestic = true)
    }

    private fun loadUsersLikeContents() {
        viewModelScope.launch {
            contentRepository
                .loadPopularFavoriteContents()
                .onSuccess { contents: List<UsersLikeContent> ->
                    _usersLikeContents.value = contents.map { it.toUiModel() }
                    _networkError.value = false
                    _serverError.value = false
                    Timber.d("인기 찜 목록: $contents")
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("인기 찜 목록 불러오기 실패")
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

    fun loadRegionCategories(isDomestic: Boolean) {
        viewModelScope.launch {
            regionRepository
                .loadRegionCategories(isDomestic)
                .onSuccess { regionCategories: List<RegionCategory> ->
                    _regionCategories.value = regionCategories
                    _isSelectedDomestic.value = isDomestic
                    _networkError.value = false
                    _serverError.value = false
                    Timber.d("지역 카테고리 조회: $regionCategories")
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("지역 카테고리 조회 실패")
                }
        }
    }

    fun updateDomesticSelected(isDomesticSelected: Boolean) {
        _isSelectedDomestic.value = isDomesticSelected
        Timber.d(if (isSelectedDomestic.value == true) "국내 클릭" else "해외 클릭")
        isSelectedDomestic.value?.let { loadRegionCategories(it) }
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
