package com.on.turip.ui.compose.turip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.model.namestatus.TuripNameStatusModel
import com.on.turip.ui.compose.turip.mapper.toEditModel
import com.on.turip.ui.compose.turip.mapper.toUiMyTuripModel
import com.on.turip.ui.compose.turip.model.DeleteTuripSnapShot
import com.on.turip.ui.compose.turip.model.MyTuripModel
import com.on.turip.ui.folder.model.TuripEditModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyTuripViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
    sessionStore: SessionStore,
) : ViewModel() {
    private val _uiState: MutableStateFlow<MyTuripUiState> =
        MutableStateFlow(MyTuripUiState.Idle)
    val uiState: StateFlow<MyTuripUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<MyTuripUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<MyTuripUiEffect> = _uiEffect.receiveAsFlow()

    private var deleteTuripSnapShot = DeleteTuripSnapShot.EMPTY

    init {
        observeSessionChange(sessionStore.state)
    }

    fun loadTurips() {
        launchWithLoading {
            turipRepository
                .loadTurips()
                .onSuccess { turips: List<Turip> ->
                    _uiState.update { myTuripUiState: MyTuripUiState ->
                        myTuripUiState.copy(
                            turips = turips.map { it.toUiMyTuripModel() }.toImmutableList(),
                            errorUiState = ErrorUiState.None,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = MyTuripRetryAction.UpdateMyTurip,
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

    fun updateInputName(name: String) {
        if (name.length > MAX_NAME_LENGTH) return
        val editModels: List<TuripEditModel> = _uiState.value.turips.map { it.toEditModel() }
        val status = TuripNameStatusModel.of(name, editModels)
        _uiState.update {
            it.copy(
                inputTuripName = name,
                turipNameStatus = status,
            )
        }
    }

    fun applyTuripDelete(turipId: Long) {
        viewModelScope.launch {
            val nextTarget: MyTuripModel? =
                commitMutex.withLock {
                    if (deleteTuripSnapShot.hasSnapshot) {
                        val pendingSnapshot = deleteTuripSnapShot
                        commitDeleteSnapshot(pendingSnapshot)
                        clearDeleteSnapshot()
                    }

                    val targetTurip: MyTuripModel =
                        uiState.value.turips.find { it.id == turipId }
                            ?: run {
                                Timber.e("삭제할 튜립을 찾을 수 없어요. turipId = $turipId")
                                return@withLock null
                            }

                    _uiState.update { state: MyTuripUiState ->
                        deleteTuripSnapShot =
                            DeleteTuripSnapShot(
                                deleteTurip = targetTurip,
                                originTurips = state.turips,
                            )
                        state.copy(
                            turips =
                                state.turips
                                    .filter { it.id != targetTurip.id }
                                    .toImmutableList(),
                        )
                    }
                    targetTurip
                }

            nextTarget ?: return@launch
            _uiEffect.send(MyTuripUiEffect.ShowTuripRemoved(nextTarget.name))
        }
    }

    private val commitMutex = Mutex()

    fun commitTuripDelete() {
        viewModelScope.launch {
            commitMutex.withLock {
                if (!deleteTuripSnapShot.hasSnapshot) {
                    Timber.e("제거할 튜립이 없어요. deleteTuripSnapShot을 확인 해주세요.")
                    return@launch
                }

                commitDeleteSnapshot(deleteTuripSnapShot)
                clearDeleteSnapshot()
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
                    .onSuccess { turip: Turip ->
                        dismissAddBottomSheet()
                        _uiEffect.send(MyTuripUiEffect.TuripAdded(name))
                        _uiState.update { it: MyTuripUiState ->
                            it.copy(
                                turips =
                                    it.turips
                                        .plus(turip.toUiMyTuripModel())
                                        .toImmutableList(),
                            )
                        }
                    }.onFailure { errorType ->
                        sendErrorEffect(errorType, MyTuripRetryAction.AddMyTurip)
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

    fun rollbackTuripDelete() {
        viewModelScope.launch {
            commitMutex.withLock {
                if (!deleteTuripSnapShot.hasSnapshot) return@launch

                val snapshot = deleteTuripSnapShot
                _uiState.update { state ->
                    state.copy(
                        turips = snapshot.originTurips,
                    )
                }
                clearDeleteSnapshot()
            }
        }
    }

    private fun clearDeleteSnapshot() {
        deleteTuripSnapShot = DeleteTuripSnapShot.EMPTY
    }

    private suspend fun commitDeleteSnapshot(snapshot: DeleteTuripSnapShot) {
        val deleteTurip = snapshot.deleteTurip
        turipRepository
            .deleteTurip(deleteTurip.id)
            .onSuccess {
                Timber.d("튜립 삭제 성공(이름 = ${deleteTurip.name})")
            }.onFailure {
                _uiState.update { state ->
                    state.copy(turips = snapshot.originTurips)
                }
                _uiEffect.send(
                    MyTuripUiEffect.ShowTuripRemoveFailed(deleteTurip.name),
                )
                Timber.e("튜립 삭제 실패(이름 = ${deleteTurip.name})")
            }
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: MyTuripRetryAction,
    ) {
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiState.update { it.copy(errorUiState = ErrorUiState.Network) }
                    _uiEffect.send(MyTuripUiEffect.ShowError(ErrorUiState.Network, retryAction))
                }

                UiError.Global.Server -> {
                    _uiState.update { it.copy(errorUiState = ErrorUiState.Server) }
                    _uiEffect.send(MyTuripUiEffect.ShowError(ErrorUiState.Server, retryAction))
                }

                UiError.Global.TokenExpired -> {
                    _uiEffect.send(MyTuripUiEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun observeSessionChange(sessionState: StateFlow<SessionState>) {
        viewModelScope.launch {
            sessionState
                .drop(1)
                .collect {
                    clearDeleteSnapshot()
                    _uiState.update { MyTuripUiState.Idle }
                }
        }
    }

    companion object {
        private const val MAX_NAME_LENGTH = 20
    }
}
