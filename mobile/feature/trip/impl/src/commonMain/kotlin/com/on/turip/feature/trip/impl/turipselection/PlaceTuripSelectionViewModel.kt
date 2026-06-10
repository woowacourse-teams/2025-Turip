package com.on.turip.feature.trip.impl.turipselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaceTuripSelectionViewModel(
    private val turipRepository: TuripRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private var originTuripIds: Set<Long> = setOf()

    private val _uiState: MutableStateFlow<PlaceTuripSelectionUiState> =
        MutableStateFlow(PlaceTuripSelectionUiState.Idle)
    val uiState: StateFlow<PlaceTuripSelectionUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<PlaceTuripSelectionUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<PlaceTuripSelectionUiEffect> = _uiEffect.receiveAsFlow()

    fun loadTuripsByPlace(
        placeId: Long,
        placeName: String,
    ) {
        if (_uiState.value.placeId == placeId && _uiState.value.turips.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorUiState = ErrorUiState.None,
                    placeId = placeId,
                    placeName = placeName,
                )
            }

            turipRepository
                .loadTuripsByPlaceId(placeId)
                .onSuccess { turips: List<Turip> ->
                    val uiModels = turips.map { it.toSelectionModel() }.toImmutableList()
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            turips = uiModels,
                            isChanged = false,
                        )
                    }
                    originTuripIds = uiModels.filter { it.isSelected }.map { it.id }.toSet()
                    Napier.d("장소에 대한 튜립 목록 바텀시트, 튜립 목록 불러오기 성공")
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isLoading = false) }
                    sendErrorEffect(errorType, PlaceTuripSelectionRetryAction.LoadTurips(placeId, placeName))
                    Napier.e("장소에 대한 튜립 목록 바텀시트, 튜립 목록 불러오기 실패")
                }
        }
    }

    fun updateTurip(turipModel: TuripSelectionModel) {
        val updateHasTuripPlace = !turipModel.isSelected
        _uiState.update { state ->
            val updateTurips =
                state.turips
                    .map { turip ->
                        if (turip.id == turipModel.id) turip.copy(isSelected = updateHasTuripPlace) else turip
                    }.toImmutableList()

            state.copy(
                turips = updateTurips,
                isChanged = isTuripChanged(updateTurips),
            )
        }
    }

    fun updateTuripsByPlace() {
        viewModelScope.launch {
            val selectedTuripIds = uiState.value.turips.filter { it.isSelected }.map { it.id }
            val placeId = uiState.value.placeId

            turipRepository
                .updatePlaceTurips(placeId, selectedTuripIds, originTuripIds)
                .onSuccess {
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.UpdateTuripsByPlace(
                            placeId = placeId,
                            hasTurip = selectedTuripIds.isNotEmpty(),
                        ),
                    )
                    _uiEffect.send(PlaceTuripSelectionUiEffect.Dismiss)
                    Napier.d("바텀시트, 선택한 장소에 대한 튜립들 현황 업데이트 성공")
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, PlaceTuripSelectionRetryAction.UpdateTuripsByPlace)
                    Napier.e("바텀시트, 선택한 장소에 대한 튜립들 현황 업데이트 실패")
                }
        }
    }

    private fun isTuripChanged(turips: List<TuripSelectionModel>): Boolean {
        val currentTuripIds = turips.filter { it.isSelected }.map { it.id }.toSet()
        return originTuripIds != currentTuripIds
    }

    fun showAddTuripBottomSheet() = _uiState.update { it.copy(showAddTuripBottomSheet = true) }

    fun dismissAddTuripBottomSheet() =
        _uiState.update {
            if (it.isCreatingTurip) {
                it.copy(showAddTuripBottomSheet = false)
            } else {
                it.copy(
                    showAddTuripBottomSheet = false,
                    addTuripInputName = "",
                    addTuripNameStatus = TuripNameStatusModel.EMPTY,
                )
            }
        }

    fun updateAddTuripInputName(name: String) {
        if (name.length > MAX_NAME_LENGTH) return
        val status = TuripNameStatusModel.of(name, persistentListOf())
        _uiState.update {
            it.copy(
                addTuripInputName = name,
                addTuripNameStatus = status,
            )
        }
    }

    fun addTurip() {
        val currentState = _uiState.value
        if (currentState.isCreatingTurip || !currentState.addTuripNameStatus.isConfirmEnabled) return
        _uiState.update { it.copy(isCreatingTurip = true) }
        val name = currentState.addTuripInputName
        viewModelScope.launch {
            turipRepository
                .createTurip(name)
                .onSuccess {
                    _uiState.update { it.copy(isCreatingTurip = false) }
                    _uiEffect.send(PlaceTuripSelectionUiEffect.TuripAdded(name))
                    _uiState.update { it.copy(turips = persistentListOf()) }
                    loadTuripsByPlace(currentState.placeId, currentState.placeName)
                }.onFailure { errorType: ErrorType ->
                    if (errorType == ErrorType.Turip.DuplicatedName) {
                        _uiState.update {
                            it.copy(
                                isCreatingTurip = false,
                                addTuripNameStatus = TuripNameStatusModel.DUPLICATE_NAME,
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isCreatingTurip = false) }
                        sendErrorEffect(errorType, PlaceTuripSelectionRetryAction.AddTurip)
                    }
                }
        }
    }

    fun handleErrorRetryRequest(action: PlaceTuripSelectionRetryAction) {
        when (action) {
            is PlaceTuripSelectionRetryAction.LoadTurips -> loadTuripsByPlace(action.placeId, action.placeName)
            PlaceTuripSelectionRetryAction.UpdateTuripsByPlace -> updateTuripsByPlace()
            PlaceTuripSelectionRetryAction.AddTurip -> addTurip()
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
                    _uiEffect.send(PlaceTuripSelectionUiEffect.ShowError(ErrorUiState.Network, retryAction))
                }

                UiError.Global.Server -> {
                    _uiEffect.send(PlaceTuripSelectionUiEffect.ShowError(ErrorUiState.Server, retryAction))
                }

                UiError.Global.TokenExpired -> {
                    sessionManager.switchToGuest()
                    _uiEffect.send(PlaceTuripSelectionUiEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun Turip.toSelectionModel(): TuripSelectionModel =
        TuripSelectionModel(
            id = id,
            name = name,
            placeCount = placeCount,
            isSelected = hasIncludePlace,
            isDefault = isDefault,
        )

    private companion object {
        private const val MAX_NAME_LENGTH = 20
    }
}
