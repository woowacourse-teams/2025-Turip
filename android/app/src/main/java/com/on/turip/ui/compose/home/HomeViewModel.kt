package com.on.turip.ui.compose.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.ErrorType
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.UiError
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onFailureWithCause
import com.on.turip.data.common.onSuccess
import com.on.turip.data.common.toUiError
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.repository.RegionRepository
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.main.home.model.UsersLikeContentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

    private val _errorUiState: MutableStateFlow<ErrorUiState> = MutableStateFlow(ErrorUiState.None)
    val errorUiState: StateFlow<ErrorUiState> = _errorUiState.asStateFlow()

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

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
                    _errorUiState.update { ErrorUiState.None }
                    Timber.d("인기 찜 목록: $contents")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("인기 찜 목록 불러오기 실패 : $errorType / $cause")
                }
        }
    }

    private fun loadRegionCategories(isDomestic: Boolean) {
        viewModelScope.launch {
            regionRepository
                .loadRegionCategories(isDomestic)
                .onSuccess { regionCategories: List<RegionCategory> ->
                    _regionCategories.value = regionCategories
                    _isSelectedDomestic.value = isDomestic
                    _errorUiState.update { ErrorUiState.None }
                    Timber.d("지역 카테고리 조회: $regionCategories")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("지역 카테고리 조회 실패 : $errorType / $cause")
                }
        }
    }

    fun updateDomesticSelected(isDomesticSelected: Boolean) {
        _isSelectedDomestic.value = isDomesticSelected
        Timber.d(if (isSelectedDomestic.value == true) "국내 클릭" else "해외 클릭")
        isSelectedDomestic.value?.let { loadRegionCategories(it) }
    }

    private suspend fun handleGlobalError(uiError: UiError.Global) {
        when (uiError) {
            UiError.Global.Network -> _errorUiState.update { ErrorUiState.Network }
            UiError.Global.Server -> _errorUiState.update { ErrorUiState.Server }
            UiError.Global.TokenExpired -> _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
        }
    }
}
