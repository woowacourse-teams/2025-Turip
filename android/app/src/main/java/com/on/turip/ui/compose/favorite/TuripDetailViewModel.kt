package com.on.turip.ui.compose.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.compose.designsystem.model.TuripNameStatusModel
import com.on.turip.ui.compose.favorite.model.DeleteTuripPlaceSnapshot
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turip.mapper.toUiMyTuripModel
import com.on.turip.ui.folder.model.TuripEditModel
import com.on.turip.ui.main.favorite.model.PlaceLatLngUiModel
import com.on.turip.ui.main.favorite.model.TuripPlaceRetryAction
import com.on.turip.ui.main.favorite.model.TuripPlaceUiEffect
import com.on.turip.ui.main.favorite.model.TuripShareModel
import com.on.turip.ui.main.favorite.toLatLng
import com.on.turip.ui.main.favorite.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TuripDetailViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TuripPlaceUiState> =
        MutableStateFlow(TuripPlaceUiState.Idle)
    val uiState: StateFlow<TuripPlaceUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TuripPlaceUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TuripPlaceUiEffect> = _uiEffect.receiveAsFlow()
    private var deleteTuripPlaceSnapshot: DeleteTuripPlaceSnapshot = DeleteTuripPlaceSnapshot.EMPTY
    private var reorderPlacesSnapshot: ImmutableList<TuripPlaceModel>? = null

    fun loadSelectedTurip(selectedTuripId: Long) {
        viewModelScope.launch {
            turipRepository.loadTurip(selectedTuripId).onSuccess { turip: Turip ->
                _uiState.update { state: TuripPlaceUiState ->
                    state.copy(
                        errorUiState = ErrorUiState.None,
                        selectedTurip = turip.toUiMyTuripModel(),
                    )
                }
            }
        }
    }

    fun loadPlaces(selectedTuripId: Long) {
        viewModelScope.launch {
            turipRepository
                .loadTuripPlaces(selectedTuripId)
                .onSuccess { result: List<TuripPlace> ->
                    _uiState.update { state: TuripPlaceUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places =
                                result
                                    .map { turipPlace: TuripPlace -> turipPlace.toUiModel() }
                                    .toImmutableList(),
                            placesLatLng =
                                result
                                    .map { it.toLatLng() }
                                    .toImmutableList(),
                        )
                    }
                    clearSnapshots()
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("튜립 장소 목록 조회 API 호출 실패")
                }
        }
    }

    fun applyTuripPlaceDelete(placeId: Long) {
        val targetPlace =
            uiState.value.places.find { it.placeId == placeId }
                ?: run {
                    Timber.e("삭제할 장소를 찾을 수 없어요. placeId = $placeId")
                    return
                }

        if (deleteTuripPlaceSnapshot.hasSnapshot) return

        _uiState.update { state: TuripPlaceUiState ->
            deleteTuripPlaceSnapshot =
                DeleteTuripPlaceSnapshot(
                    deletePlace = targetPlace,
                    originPlaces = state.places,
                    originPlacesLatLng = state.placesLatLng,
                )
            state.copy(
                places =
                    state.places
                        .filter { it.turipPlaceId != targetPlace.turipPlaceId }
                        .toImmutableList(),
                placesLatLng =
                    state.placesLatLng
                        .filter { it.placeId != targetPlace.placeId }
                        .toImmutableList(),
            )
        }

        viewModelScope.launch {
            _uiEffect.send(
                TuripPlaceUiEffect.ShowTuripPlaceRemoved(targetPlace.name),
            )
        }
    }

    private val commitMutex = Mutex()

    fun commitTuripPlaceDelete() {
        viewModelScope.launch {
            commitMutex.withLock {
                if (!deleteTuripPlaceSnapshot.hasSnapshot) {
                    Timber.e("제거할 장소에 대한 정보가 없어요. deletePlaceSnapshot을 확인 해주세요 ")
                    return@launch
                }

                val deletePlace = deleteTuripPlaceSnapshot.deletePlace
                turipRepository
                    .deleteTuripPlace(uiState.value.selectedTurip.id, deletePlace.placeId)
                    .onSuccess {
                        Timber.d("튜립 상세 바텀시트, 장소 업데이트 성공")
                    }.onFailure {
                        _uiEffect.send(
                            TuripPlaceUiEffect.ShowTuripPlaceRemoveFailed(deletePlace.name),
                        )
                        _uiState.update { it.copy(places = deleteTuripPlaceSnapshot.originPlaces) }
                        Timber.e("튜립 상세 바텀시트, 장소 업데이트 실패 place = ${deletePlace.name}")
                    }

                deleteTuripPlaceSnapshot = DeleteTuripPlaceSnapshot.EMPTY
            }
        }
    }

    fun updateScreenMode(turipPlaceScreenMode: TuripPlaceScreenMode) {
        if (turipPlaceScreenMode == TuripPlaceScreenMode.MoreOption) {
            _uiState.update { it.copy(inputTuripName = "") }
        }

        _uiState.update { it.copy(screenMode = turipPlaceScreenMode) }
    }

    fun updateInputName(name: String) {
        if (name.length > MAX_NAME_LENGTH) return
        val editModel: ImmutableList<TuripEditModel> = _uiState.value.editModels
        val status: TuripNameStatusModel = TuripNameStatusModel.of(name, editModel)
        _uiState.update {
            it.copy(
                inputTuripName = name,
                turipNameStatus = status,
            )
        }
    }

    fun updateTuripName() {
        viewModelScope.launch {
            turipRepository
                .updateTurip(uiState.value.selectedTurip.id, uiState.value.inputTuripName)
                .onSuccess {
                    _uiState.update { state: TuripPlaceUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            selectedTurip = uiState.value.selectedTurip.copy(name = uiState.value.inputTuripName),
                            inputTuripName = "",
                        )
                    }
                    _uiEffect.send(TuripPlaceUiEffect.TuripUpdated)
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, TuripPlaceRetryAction.TuripNameUpdate)
                }
        }
    }

    fun updateSelectedPlace(placeId: Long) {
        _uiState.update { turipPlaceUiState: TuripPlaceUiState ->
            turipPlaceUiState.copy(
                selectedPlace =
                    _uiState.value.placesLatLng.find { it.placeId == placeId }
                        ?: throw IllegalStateException("장소를 찾을 수 없습니다."),
            )
        }
    }

    fun resetUiState() = _uiState.update { TuripPlaceUiState.Idle }

    fun showBottomSheet() = _uiState.update { it.copy(showBottomSheet = true) }

    fun dismissBottomSheet() =
        _uiState.update {
            it.copy(
                showBottomSheet = false,
                screenMode = TuripPlaceScreenMode.MoreOption,
            )
        }

    fun showTuripRemoveDialog() = _uiState.update { it.copy(showTuripRemoveDialog = true) }

    fun dismissTuripRemoveDialog() = _uiState.update { it.copy(showTuripRemoveDialog = false) }

    fun deleteTurip(selectedTuripId: Long) {
        viewModelScope.launch {
            turipRepository
                .deleteTurip(selectedTuripId)
                .onSuccess {
                    _uiState.update { state: TuripPlaceUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                        )
                    }
                    _uiEffect.send(TuripPlaceUiEffect.TuripDelete)
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, TuripPlaceRetryAction.TuripDelete)
                }
        }
    }

    fun updateTuripPlacesOrder(updateTuripPlaces: ImmutableList<TuripPlaceModel>) {
        reorderPlacesSnapshot = uiState.value.places
        _uiState.update { state ->
            state.copy(
                places = updateTuripPlaces,
                placesLatLng =
                    updateTuripPlaces
                        .map { it.toPlaceLatLngUiModel() }
                        .toImmutableList(),
            )
        }

        viewModelScope.launch {
            turipRepository
                .updateTuripPlacesOrder(
                    turipId = uiState.value.selectedTurip.id,
                    updatedOrder = updateTuripPlaces.map { it.turipPlaceId },
                ).onSuccess {
                    clearReorderSnapshot()
                }.onFailure { errorType: ErrorType ->
                    rollbackReorderedPlaces()
                    Timber.e("장소 순서 변경 API 호출 실패 ")
                }
        }
    }

    fun shareTurip() {
        when (AuthState.type) {
            UserType.MEMBER -> {
                val turipShareModel =
                    TuripShareModel(
                        name = uiState.value.selectedTurip.name,
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
                loadPlaces(action.placeId)
            }

            TuripPlaceRetryAction.TuripDelete -> {
                deleteTurip(uiState.value.selectedTurip.id)
            }

            TuripPlaceRetryAction.TuripNameUpdate -> {
                updateTuripName()
            }
        }
    }

    fun rollbackTuripPlaceDelete() {
        if (!deleteTuripPlaceSnapshot.hasSnapshot) return

        _uiState.update { state ->
            state.copy(
                places = deleteTuripPlaceSnapshot.originPlaces,
                placesLatLng = deleteTuripPlaceSnapshot.originPlacesLatLng,
            )
        }
        clearDeleteSnapshot()
    }

    private fun rollbackReorderedPlaces() {
        val snapshot: ImmutableList<TuripPlaceModel> = reorderPlacesSnapshot ?: return
        _uiState.update { state ->
            state.copy(
                places = snapshot,
                placesLatLng = snapshot.map { it.toPlaceLatLngUiModel() }.toImmutableList(),
            )
        }
        clearReorderSnapshot()
    }

    private fun TuripPlaceModel.toPlaceLatLngUiModel(): PlaceLatLngUiModel =
        PlaceLatLngUiModel(
            placeId = placeId,
            name = name,
            latLng = latLng,
        )

    private fun clearSnapshots() {
        clearDeleteSnapshot()
        clearReorderSnapshot()
    }

    private fun clearDeleteSnapshot() {
        deleteTuripPlaceSnapshot = DeleteTuripPlaceSnapshot.EMPTY
    }

    private fun clearReorderSnapshot() {
        reorderPlacesSnapshot = null
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: TuripPlaceRetryAction,
    ) {
        _uiState.update { it.copy(isLoading = false) }
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiEffect.send(TuripPlaceUiEffect.ShowError(ErrorUiState.Network, retryAction))
                }

                UiError.Global.Server -> {
                    _uiEffect.send(TuripPlaceUiEffect.ShowError(ErrorUiState.Server, retryAction))
                }

                UiError.Global.TokenExpired -> {
                    _uiEffect.send(TuripPlaceUiEffect.NavigateToLogin)
                }
            }
        }
    }

    companion object {
        private const val MAX_NAME_LENGTH = 20
    }
}
