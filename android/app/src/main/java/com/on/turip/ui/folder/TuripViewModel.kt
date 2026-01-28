package com.on.turip.ui.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.folder.Turip
import com.on.turip.domain.folder.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.mapper.toEditUiModel
import com.on.turip.ui.folder.model.TuripEditModel
import com.on.turip.ui.folder.model.TuripNameStatusModel
import com.on.turip.ui.folder.model.TuripRetryAction
import com.on.turip.ui.folder.model.TuripUiEffect
import com.on.turip.ui.folder.model.TuripUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TuripViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TuripUiState> = MutableStateFlow(TuripUiState.Idle)
    val uiState: StateFlow<TuripUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TuripUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TuripUiEffect> = _uiEffect.receiveAsFlow()

    private val inputTuripName: MutableStateFlow<String> = MutableStateFlow("")

    val turipNameStatus: StateFlow<TuripNameStatusModel> =
        combine(inputTuripName, uiState) { name, uiState ->
            TuripNameStatusModel.of(name, uiState.turips)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TuripNameStatusModel.EMPTY,
        )

    init {
        loadTurips()
    }

    fun loadTurips() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            turipRepository
                .loadTurips()
                .onSuccess { turips: List<Turip> ->
                    Timber.d("튜립 목록 설정 화면, 튜립 목록 불러오기 성공")
                    _uiState.update { state: TuripUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            turips =
                                turips
                                    .filter { !it.isDefault }
                                    .map { turip -> turip.toEditUiModel() },
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
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
                                _uiEffect.send(TuripUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.e("튜립 목록 설정 화면, 튜립 목록 불러오기 실패")
                }
        }
    }

    fun updateTuripName(input: String) {
        inputTuripName.update { input }
    }

    fun addTurip() {
        viewModelScope.launch {
            turipRepository
                .createTurip(inputTuripName.value)
                .onSuccess {
                    Timber.d("새 튜립 생성 완료(이름 = ${inputTuripName.value})")
                    _uiState.update { state: TuripUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            turips = state.turips.plus(it.toEditUiModel()),
                        )
                    }
                    inputTuripName.update { "" }
                    _uiEffect.send(TuripUiEffect.TuripAdded)
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, TuripRetryAction.TuripAdd)
                    Timber.e("새 튜립 생성 실패(이름 = ${inputTuripName.value})")
                }
        }
    }

    fun selectTurip(turipId: Long) {
        _uiState.update { state: TuripUiState ->
            state.copy(turips = state.turips.map { it.copy(isSelected = it.id == turipId) })
        }
    }

    fun updateTuripName() {
        val selectTurip: TuripEditModel = getSelectedTuripOrNull() ?: return
        viewModelScope.launch {
            turipRepository
                .updateTurip(selectTurip.id, inputTuripName.value)
                .onSuccess {
                    Timber.d("튜립명 수정 완료(이름 = ${selectTurip.name} -> ${inputTuripName.value})")
                    _uiState.update { state: TuripUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            turips =
                                state.turips.map { turip: TuripEditModel ->
                                    if (turip.id == selectTurip.id) {
                                        turip.copy(name = inputTuripName.value)
                                    } else {
                                        turip
                                    }
                                },
                        )
                    }
                    inputTuripName.update { "" }
                    _uiEffect.send(TuripUiEffect.TuripUpdated)
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, TuripRetryAction.TuripNameUpdate)
                    Timber.e("튜립명 수정 실패(기존 튜립명 = ${selectTurip.name}) ")
                }
        }
    }

    fun deleteTurip() {
        val selectTurip: TuripEditModel = getSelectedTuripOrNull() ?: return
        viewModelScope.launch {
            turipRepository
                .deleteTurip(selectTurip.id)
                .onSuccess {
                    _uiState.update { state: TuripUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            turips = state.turips.filter { it.id != selectTurip.id },
                        )
                    }
                    Timber.d("튜립 삭제 완료(이름 = ${selectTurip.name})")
                    _uiEffect.send(TuripUiEffect.TuripDeleted)
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, TuripRetryAction.TuripDelete)
                    Timber.e("튜립 삭제 실패(이름 = ${selectTurip.name}")
                }
        }
    }

    fun getSelectedTuripOrNull(): TuripEditModel? = uiState.value.turips.firstOrNull { it.isSelected }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: TuripRetryAction,
    ) {
        _uiState.update { it.copy(isLoading = false) }
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiEffect.send(TuripUiEffect.ShowError(ErrorUiState.Network, retryAction))
                }

                UiError.Global.Server -> {
                    _uiEffect.send(TuripUiEffect.ShowError(ErrorUiState.Server, retryAction))
                }

                UiError.Global.TokenExpired -> {
                    _uiEffect.send(TuripUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun handleErrorRetryRequest(action: TuripRetryAction) {
        when (action) {
            TuripRetryAction.TuripAdd -> addTurip()
            TuripRetryAction.TuripNameUpdate -> updateTuripName()
            TuripRetryAction.TuripDelete -> deleteTurip()
        }
    }
}
