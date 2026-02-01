package com.on.turip.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.bookmark.usecase.UpdateTuripPlaceUseCase
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.main.favorite.model.TuripModel
import com.on.turip.ui.main.favorite.model.TuripPlaceRetryAction
import com.on.turip.ui.main.favorite.model.TuripPlaceUiEffect
import com.on.turip.ui.main.favorite.model.TuripPlaceUiModel
import com.on.turip.ui.main.favorite.model.TuripPlaceUiState
import com.on.turip.ui.main.favorite.model.TuripShareModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TuripPlaceViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
    private val updateTuripPlaceUseCase: UpdateTuripPlaceUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TuripPlaceUiState> =
        MutableStateFlow(TuripPlaceUiState.Idle)
    val uiState: StateFlow<TuripPlaceUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TuripPlaceUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TuripPlaceUiEffect> = _uiEffect.receiveAsFlow()

    private var selectedTuripId: Long = NOT_INITIALIZED

    fun loadTuripsAndPlaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            turipRepository
                .loadTurips()
                .onSuccess { turips: List<Turip> ->
                    Timber.d("튜립 불러오기 성공")
                    ensureValidSelectedTuripId(turips)

                    val loadTurips =
                        turips.map { turip: Turip -> turip.toUiModel(selectTuripId = selectedTuripId) }

                    when (
                        val result: TuripResult<List<TuripPlace>> =
                            turipRepository.loadTuripPlaces(selectedTuripId)
                    ) {
                        is TuripResult.Success -> {
                            _uiState.update { state: TuripPlaceUiState ->
                                state.copy(
                                    isLoading = false,
                                    errorUiState = ErrorUiState.None,
                                    places = result.value.map { turipPlace: TuripPlace -> turipPlace.toModel() },
                                    turips = loadTurips,
                                    placesLatLng = result.value.map { it.toLatLng() },
                                )
                            }
                        }

                        is TuripResult.Failure -> {
                            when (val uiError: UiError = result.errorType.toUiError()) {
                                is UiError.Global -> handleGlobalError(uiError)
                                is UiError.Feature -> Unit
                            }
                            Timber.e("튜립 장소 목록 조회 API 호출 실패")
                        }
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("튜립 목록 조회 API 호출 실패")
                }
        }
    }

    private fun ensureValidSelectedTuripId(turips: List<Turip>) {
        if (selectedTuripId == NOT_INITIALIZED || turips.all { it.id != selectedTuripId }) {
            selectedTuripId = turips.firstOrNull { it.name == DEFAULT_TURIP_NAME }?.id
                ?: turips.firstOrNull()?.id ?: NOT_INITIALIZED
        }
    }

    fun updateTuripPlace(
        placeId: Long,
        isTuripPlace: Boolean,
    ) {
        val updatedIsTuripPlace: Boolean = !isTuripPlace
        viewModelScope.launch {
            updateTuripPlaceUseCase(selectedTuripId, placeId, updatedIsTuripPlace)
                .onSuccess {
                    _uiState.update { state: TuripPlaceUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = state.places.filter { it.placeId != placeId },
                            placesLatLng = state.placesLatLng.filter { it.placeId != placeId },
                        )
                    }
                    Timber.d("튜립 내 튜립 장소 상태 업데이트 성공, turipId = $selectedTuripId placeId = $placeId")
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isLoading = false) }
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(
                                    TuripPlaceUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Network,
                                        retryAction =
                                            TuripPlaceRetryAction
                                                .UpdateTuripPlace(placeId, isTuripPlace),
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(
                                    TuripPlaceUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Server,
                                        retryAction =
                                            TuripPlaceRetryAction
                                                .UpdateTuripPlace(placeId, isTuripPlace),
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _uiEffect.send(TuripPlaceUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.d("튜립 내 튜립 장소 상태 업데이트 실패, turipId = $selectedTuripId placeId = $placeId originIsTuripPlace =$isTuripPlace")
                }
        }
    }

    fun updateTuripWithPlaces(turipId: Long) {
        if (turipId == selectedTuripId) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            turipRepository
                .loadTuripPlaces(turipId)
                .onSuccess { turipPlaces: List<TuripPlace> ->
                    selectedTuripId = turipId
                    _uiState.update { state: TuripPlaceUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = turipPlaces.map { it.toModel() },
                            turips =
                                state.turips.map { turip: TuripModel ->
                                    turip.copy(isSelected = turip.id == turipId)
                                },
                            placesLatLng = turipPlaces.map { it.toLatLng() },
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("튜립에 포함된 장소 불러오는 API 호출 실패 turipId =$turipId")
                }
        }
    }

    fun updateTuripPlacesOrder(updateTuripPlaces: List<TuripPlaceUiModel>) {
        viewModelScope.launch {
            turipRepository
                .updateTuripPlacesOrder(
                    turipId = selectedTuripId,
                    updatedOrder = updateTuripPlaces.map { it.turipPlaceId },
                ).onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = updateTuripPlaces,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("장소 순서 변경 API 호출 실패 ")
                }
        }
    }

    fun shareTurip() {
        when (AuthState.type) {
            UserType.MEMBER -> {
                val turipShareModel =
                    TuripShareModel(
                        name =
                            uiState.value.turips
                                .first { it.isSelected }
                                .name,
                        places = uiState.value.places.map { it.toUiModel() },
                    )
                viewModelScope.launch {
                    _uiEffect.send(TuripPlaceUiEffect.ShareTurip(turipShareModel))
                }
            }

            UserType.GUEST, UserType.NONE -> {
                viewModelScope.launch {
                    _uiEffect.send(TuripPlaceUiEffect.ShowTuripShareNotAllowed)
                }
            }
        }
    }

    private suspend fun handleGlobalError(uiError: UiError.Global) {
        when (uiError) {
            UiError.Global.Network -> {
                _uiState.update {
                    it.copy(isLoading = false, errorUiState = ErrorUiState.Network)
                }
            }

            UiError.Global.Server -> {
                _uiState.update {
                    it.copy(isLoading = false, errorUiState = ErrorUiState.Server)
                }
            }

            UiError.Global.TokenExpired -> {
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.send(TuripPlaceUiEffect.NavigateToLogin)
            }
        }
    }

    fun handleErrorRetryRequest(action: TuripPlaceRetryAction) {
        when (action) {
            is TuripPlaceRetryAction.UpdateTuripPlace -> {
                updateTuripPlace(action.placeId, action.isTuripPlace)
            }
        }
    }

    companion object {
        private const val NOT_INITIALIZED: Long = 0L
        private const val DEFAULT_TURIP_NAME = "기본 튜립"
    }
}
