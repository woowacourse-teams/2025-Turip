package com.on.turip.ui.main.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.favorite.usecase.UpdateTuripPlaceUseCase
import com.on.turip.domain.folder.Turip
import com.on.turip.domain.folder.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.main.favorite.PlaceTuripSelectionFragment.Companion.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID
import com.on.turip.ui.main.favorite.model.PlaceTuripSelectionRetryAction
import com.on.turip.ui.main.favorite.model.PlaceTuripSelectionUiEffect
import com.on.turip.ui.main.favorite.model.PlaceTuripSelectionUiState
import com.on.turip.ui.main.favorite.model.TuripModel
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
class PlaceTuripSelectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val turipRepository: TuripRepository,
    private val updateTuripPlaceUseCase: UpdateTuripPlaceUseCase,
) : ViewModel() {
    private val placeId: Long by lazy {
        checkNotNull(savedStateHandle[FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID]) {
            Timber.e("장소에 대한 튜립 목록 바텀시트, place ID 값이 존재하지 않습니다.")
        }
    }

    private val _uiState: MutableStateFlow<PlaceTuripSelectionUiState> =
        MutableStateFlow(PlaceTuripSelectionUiState.Idle)
    val uiState: StateFlow<PlaceTuripSelectionUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<PlaceTuripSelectionUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<PlaceTuripSelectionUiEffect> = _uiEffect.receiveAsFlow()

    fun loadTuripsByPlace() {
        viewModelScope.launch {
            turipRepository
                .loadTuripsByPlaceId(placeId)
                .onSuccess { turips: List<Turip> ->
                    _uiState.update { state: PlaceTuripSelectionUiState ->
                        state.copy(
                            placeId = placeId,
                            turips = turips.map { it.toUiModel() },
                        )
                    }
                    Timber.d("장소에 대한 튜립 목록 바텀시트, 튜립 목록 불러오기 성공")
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = PlaceTuripSelectionRetryAction.LoadTurips,
                    )
                    Timber.e("장소에 대한 튜립 목록 바텀시트, 튜립 목록 불러오기 실패")
                }
        }
    }

    fun updateTurip(turipModel: TuripModel) {
        viewModelScope.launch {
            val updateHasTuripPlace: Boolean = !turipModel.isSelected
            updateTuripPlaceUseCase(turipModel.id, placeId, updateHasTuripPlace)
                .onSuccess {
                    Timber.d("장소에 대한 튜립 목록 바텀시트, 튜립 목록 업데이트 성공 turipName = ${turipModel.name}, hasPlaceInTurip = $updateHasTuripPlace")
                    _uiState.update { state: PlaceTuripSelectionUiState ->
                        state.copy(
                            turips =
                                state.turips.map { folder ->
                                    if (folder.id == turipModel.id) {
                                        folder.copy(isSelected = !folder.isSelected)
                                    } else {
                                        folder
                                    }
                                },
                        )
                    }
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.ShowUpdateTurip(turipModel),
                    )
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction =
                            PlaceTuripSelectionRetryAction.UpdateTurip(turipModel),
                    )
                    Timber.e(
                        "장소에 대한 튜립 목록 바텀시트, 튜립 목록 업데이트 실패 turipName = ${turipModel.name}, originHasTuripPlace = ${turipModel.isSelected}",
                    )
                }
        }
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: PlaceTuripSelectionRetryAction,
    ) {
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.ShowError(ErrorUiState.Network, retryAction),
                    )
                }

                UiError.Global.Server -> {
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.ShowError(ErrorUiState.Server, retryAction),
                    )
                }

                UiError.Global.TokenExpired -> {
                    _uiEffect.send(PlaceTuripSelectionUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun handleErrorRetryRequest(action: PlaceTuripSelectionRetryAction) {
        when (action) {
            PlaceTuripSelectionRetryAction.LoadTurips -> loadTuripsByPlace()
            is PlaceTuripSelectionRetryAction.UpdateTurip -> updateTurip(action.turipModel)
        }
    }
}
