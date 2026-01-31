package com.on.turip.ui.main.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.bookmark.usecase.UpdateTuripPlaceUseCase
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.TURIP_CATALOG_ARGUMENTS_TURIP_ID
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.TURIP_CATALOG_ARGUMENTS_TURIP_NAME
import com.on.turip.ui.main.favorite.model.TuripCatalogRetryAction
import com.on.turip.ui.main.favorite.model.TuripCatalogUiEffect
import com.on.turip.ui.main.favorite.model.TuripCatalogUiState
import com.on.turip.ui.main.favorite.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.TuripShareModel
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
class TuripCatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val turipRepository: TuripRepository,
    private val updateTuripPlaceUseCase: UpdateTuripPlaceUseCase,
) : ViewModel() {
    private val turipId: Long by lazy {
        checkNotNull(savedStateHandle[TURIP_CATALOG_ARGUMENTS_TURIP_ID]) {
            Timber.e("튜립 내 장소 목록 바텀 시트, 튜립 ID 값이 존재하지 않습니다.")
        }
    }
    private val turipName: String by lazy {
        checkNotNull(savedStateHandle[TURIP_CATALOG_ARGUMENTS_TURIP_NAME]) {
            Timber.e("튜립 내 장소 목록 바텀 시트, 튜립 이름이 존재하지 않습니다.")
        }
    }

    private val _uiState: MutableStateFlow<TuripCatalogUiState> =
        MutableStateFlow(TuripCatalogUiState.Idle)
    val uiState: StateFlow<TuripCatalogUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TuripCatalogUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TuripCatalogUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadPlacesInSelectTurip()
    }

    private fun loadPlacesInSelectTurip() {
        viewModelScope.launch {
            turipRepository
                .loadTuripPlaces(turipId)
                .onSuccess { turipPlaces: List<TuripPlace> ->
                    _uiState.update { state: TuripCatalogUiState ->
                        state.copy(
                            places = turipPlaces.map { it.toUiModel() },
                            turipName = turipName,
                        )
                    }
                    Timber.d("튜립 내 장소 목록 바텀 시트, 장소 목록 불러오기 성공")
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        handleGlobalError(
                            uiError = uiError,
                            retryAction = TuripCatalogRetryAction.LoadPlacesInTurip,
                        )
                        Timber.d("튜립 내 장소 목록 바텀 시트, 장소 목록 불러오기 실패")
                    }
                }
        }
    }

    fun updateTuripPlace(
        placeId: Long,
        isTuripPlace: Boolean,
    ) {
        val updatedIsTuripPlace: Boolean = !isTuripPlace
        viewModelScope.launch {
            updateTuripPlaceUseCase(turipId, placeId, updatedIsTuripPlace)
                .onSuccess {
                    _uiState.update { state: TuripCatalogUiState ->
                        state.copy(places = state.places.filter { it.placeId != placeId })
                    }
                    Timber.d("튜립 장소 목록 바텀 시트, 튜립 장소 상태 업데이트 성공")
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        handleGlobalError(
                            uiError = uiError,
                            retryAction = TuripCatalogRetryAction.LoadPlacesInTurip,
                        )
                    }
                    Timber.e("튜립 장소 목록 바텀 시트, 튜립 장소 상태 업데이트 실패 (placeId = $placeId")
                }
        }
    }

    fun updateTuripPlacesOrder(updateTuripPlaces: List<TuripPlaceModel>) {
        viewModelScope.launch {
            turipRepository
                .updateTuripPlacesOrder(
                    turipId = turipId,
                    updatedOrder = updateTuripPlaces.map { it.turipPlaceId },
                ).onSuccess {
                    _uiState.update { it.copy(places = updateTuripPlaces) }
                    Timber.d("튜립 장소 목록 바텀 시트, 튜립 장소 순서 변경 완료")
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        handleGlobalError(
                            uiError = uiError,
                            retryAction = TuripCatalogRetryAction.LoadPlacesInTurip,
                        )
                    }
                    Timber.e("튜립 장소 목록 바텀 시트, 튜립 장소 순서 변경 실패 ")
                }
        }
    }

    fun shareFolder() {
        when (AuthState.type) {
            UserType.MEMBER -> {
                val turipShareModel =
                    TuripShareModel(
                        name = turipName,
                        places = uiState.value.places.map { it.toUiModel() },
                    )
                viewModelScope.launch {
                    _uiEffect.send(
                        TuripCatalogUiEffect.ShareTurip(turipShareModel),
                    )
                }
            }

            UserType.GUEST, UserType.NONE -> {
                viewModelScope.launch {
                    _uiEffect.send(TuripCatalogUiEffect.ShowTuripShareNotAllowed)
                }
            }
        }
    }

    private suspend fun handleGlobalError(
        uiError: UiError.Global,
        retryAction: TuripCatalogRetryAction,
    ) {
        when (uiError) {
            UiError.Global.Network -> {
                _uiEffect.send(
                    TuripCatalogUiEffect.ShowError(ErrorUiState.Network, retryAction),
                )
            }

            UiError.Global.Server -> {
                _uiEffect.send(
                    TuripCatalogUiEffect.ShowError(ErrorUiState.Server, retryAction),
                )
            }

            UiError.Global.TokenExpired -> {
                _uiEffect.send(TuripCatalogUiEffect.NavigateToLogin)
            }
        }
    }

    fun handleErrorRetryRequest(action: TuripCatalogRetryAction) {
        when (action) {
            TuripCatalogRetryAction.LoadPlacesInTurip -> loadPlacesInSelectTurip()
        }
    }
}
