package com.on.turip.feature.turip.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.domain.usecase.DeleteTuripUseCase
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.model.turip.TuripType
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.core.ui.error.toUiModel
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.core.ui.model.turip.TuripEditModel
import com.on.turip.feature.turip.impl.mapper.toEditModel
import com.on.turip.feature.turip.impl.mapper.toUiMyTuripModel
import com.on.turip.feature.turip.impl.model.ErrorPresentation
import com.on.turip.feature.turip.impl.model.MyTuripModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyTuripViewModel(
    private val turipRepository: TuripRepository,
    private val deleteTuripUseCase: DeleteTuripUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<MyTuripUiState> =
        MutableStateFlow(MyTuripUiState.Idle)
    val uiState: StateFlow<MyTuripUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<MyTuripUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<MyTuripUiEffect> = _uiEffect.receiveAsFlow()

    init {
        observeTurips()
        loadTurips()
        observeSessionChange(sessionManager.state)
    }

    private fun observeTurips() {
        turipRepository.turips
            .onEach { turips ->
                _uiState.update { state ->
                    state.copy(turips = turips.map { it.toUiMyTuripModel() }.toImmutableList())
                }
            }.launchIn(viewModelScope)
    }

    fun loadTurips() {
        launchWithLoading {
            turipRepository
                .loadTurips()
                .onSuccess {
                    _uiState.update { it.copy(errorUiState = ErrorUiState.None) }
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = MyTuripRetryAction.UpdateMyTurip,
                        presentation = ErrorPresentation.FullScreen,
                    )
                }
        }
    }

    fun showAddBottomSheet() = _uiState.update { it.copy(showAddBottomSheet = true) }

    fun dismissAddBottomSheet() =
        _uiState.update {
            it.copy(
                showAddBottomSheet = false,
                isCreatingTurip = false,
                turipNameStatus = TuripNameStatusModel.EMPTY,
                inputTuripName = "",
            )
        }

    fun showTuripRemoveDialog(myTuripModel: MyTuripModel) =
        _uiState.update { it.copy(dialogState = MyTuripUiState.MyTuripDialogState.RemoveTurip(turip = myTuripModel)) }

    fun dismissTuripRemoveDialog() = _uiState.update { it.copy(dialogState = null) }

    // 함께 튜립명은 나홀로 튜립 또는 함께 튜립과 중복될 수 있으므로 중복 검사 대상이 아니다.
    // 길이 제한은 NameEditorSheetContent 의 InputTransformation 이 담당한다.
    // 여기서 입력을 되돌리면 iOS 한글 IME 의 조합이 깨진다.
    fun updateInputName(name: String) {
        val editModels: List<TuripEditModel> =
            _uiState.value.turips
                .filter { it.type != TuripType.TOGETHER }
                .map { it.toEditModel() }

        val status = TuripNameStatusModel.of(name, editModels)
        _uiState.update {
            it.copy(
                inputTuripName = name,
                turipNameStatus = status,
            )
        }
    }

    fun deleteTurip(turipId: Long) {
        viewModelScope.launch {
            val targetTurip =
                uiState.value.turips.find { it.id == turipId }
                    ?: run {
                        Napier.e("삭제할 튜립을 찾을 수 없어요. turipId = $turipId")
                        return@launch
                    }

            deleteTuripUseCase(
                turipId = targetTurip.id,
                type = targetTurip.type,
            ).onSuccess {
                _uiEffect.send(MyTuripUiEffect.TuripDeleted(targetTurip.name))
                Napier.d("튜립 삭제 성공(이름 = ${targetTurip.name})")
            }.onFailure { errorType: ErrorType ->
                sendErrorEffect(
                    errorType = errorType,
                    retryAction = MyTuripRetryAction.DeleteMyTurip(turipId),
                    presentation = ErrorPresentation.FullScreen,
                )
                Napier.e("튜립 삭제 실패(이름 = ${targetTurip.name})")
            }
        }
    }

    fun addTurip() {
        val currentState = uiState.value
        if (currentState.isCreatingTurip || !currentState.turipNameStatus.isConfirmEnabled) return

        _uiState.update { it.copy(isCreatingTurip = true) }
        val name = currentState.inputTuripName

        viewModelScope.launch {
            try {
                turipRepository
                    .createTurip(name)
                    .onSuccess {
                        dismissAddBottomSheet()
                        _uiEffect.send(MyTuripUiEffect.TuripAdded(name))
                    }.onFailure { errorType ->
                        sendErrorEffect(
                            errorType = errorType,
                            retryAction = MyTuripRetryAction.AddMyTurip,
                            presentation = ErrorPresentation.BottomSheetSnackbar,
                        )
                    }
            } finally {
                _uiState.update { it.copy(isCreatingTurip = false) }
            }
        }
    }

    fun handleErrorRetryRequest(action: MyTuripRetryAction) {
        when (action) {
            is MyTuripRetryAction.UpdateMyTurip -> {
                loadTurips()
            }

            is MyTuripRetryAction.AddMyTurip -> {
                addTurip()
            }

            is MyTuripRetryAction.DeleteMyTurip -> {
                deleteTurip(action.turipId)
            }
        }
    }

    private fun launchWithLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                block()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: MyTuripRetryAction,
        presentation: ErrorPresentation,
    ) {
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    handleErrorPresentation(presentation, retryAction, ErrorUiState.Network)
                }

                UiError.Global.Server -> {
                    handleErrorPresentation(presentation, retryAction, ErrorUiState.Server)
                }

                UiError.Global.TokenExpired -> {
                    sessionManager.switchToGuest()
                    _uiEffect.send(MyTuripUiEffect.NavigateToLogin)
                }
            }
        }
    }

    private suspend fun handleErrorPresentation(
        presentation: ErrorPresentation,
        retryAction: MyTuripRetryAction,
        errorUiState: ErrorUiState,
    ) {
        _uiState.update { it.copy(errorUiState = errorUiState) }
        when (presentation) {
            ErrorPresentation.FullScreen -> {
                _uiEffect.send(
                    MyTuripUiEffect.ShowError(
                        errorUiState.toUiModel(),
                        retryAction,
                    ),
                )
            }

            ErrorPresentation.BottomSheetSnackbar -> {
                _uiEffect.send(
                    MyTuripUiEffect.ShowBottomSheetError(
                        errorUiState.toUiModel(),
                        retryAction,
                    ),
                )
            }
        }
    }

    private fun observeSessionChange(sessionState: StateFlow<SessionState>) {
        viewModelScope.launch {
            sessionState
                .drop(1)
                .collect {
                    _uiState.update { MyTuripUiState.Idle }
                }
        }
    }
}
