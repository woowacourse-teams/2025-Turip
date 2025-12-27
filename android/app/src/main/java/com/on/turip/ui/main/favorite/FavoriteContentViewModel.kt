package com.on.turip.ui.main.favorite

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
import com.on.turip.domain.favorite.FavoriteContent
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import com.on.turip.ui.common.event.CommonUiEffect
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
class FavoriteContentViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val _favoriteContents: MutableLiveData<List<FavoriteContent>> =
        MutableLiveData(emptyList())
    val favoriteContents: LiveData<List<FavoriteContent>> get() = _favoriteContents

    private val _errorUiState: MutableStateFlow<ErrorUiState> = MutableStateFlow(ErrorUiState.None)
    val errorUiState: StateFlow<ErrorUiState> = _errorUiState.asStateFlow()

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    init {
        loadFavoriteContents()
    }

    fun loadFavoriteContents() {
        viewModelScope.launch {
            favoriteRepository
                .loadFavoriteContents(10, 0L)
                .onSuccess {
                    Timber.d("찜 목록 데이터 조회 성공")
                    _favoriteContents.value = it.favoriteContents
                    _errorUiState.update { ErrorUiState.None }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("찜 목록 데이터 조회 에러 발생 : $errorType / $cause")
                }
        }
    }

    fun updateFavorite(
        contentId: Long,
        isFavorite: Boolean,
    ) {
        val updatedFavorite: Boolean = !isFavorite

        viewModelScope.launch {
            updateFavoriteUseCase(updatedFavorite, contentId)
                .onSuccess {
                    Timber.d("찜 목록 페이지, 찜 버튼 클릭(contentId=$contentId, updateFavorite = $updatedFavorite")
                    _favoriteContents.value =
                        favoriteContents.value?.filter { it.content.id != contentId }
                    _errorUiState.update { ErrorUiState.None }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("찜 목록에서 찜 버튼 클릭 업데이트 실패 : $errorType / $cause")
                }
        }
    }

    private suspend fun handleGlobalError(uiError: UiError.Global) {
        when (uiError) {
            UiError.Global.Network -> _errorUiState.update { ErrorUiState.Network }
            UiError.Global.Server -> _errorUiState.update { ErrorUiState.Server }
            UiError.Global.TokenExpired -> _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
        }
    }
}
