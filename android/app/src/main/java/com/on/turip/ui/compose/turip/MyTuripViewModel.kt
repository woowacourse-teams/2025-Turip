package com.on.turip.ui.compose.turip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.designsystem.model.TuripNameStatusModel
import com.on.turip.ui.compose.turip.mapper.toEditModel
import com.on.turip.ui.compose.turip.mapper.toUiMyTuripModel
import com.on.turip.ui.folder.model.TuripEditModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
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
class MyTuripViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<MyTuripUiState> =
        MutableStateFlow(MyTuripUiState.Idle)
    val uiState: StateFlow<MyTuripUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<MyTuripUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<MyTuripUiEffect> = _uiEffect.receiveAsFlow()

    fun loadTuripFolders() {
        viewModelScope.launch {
            turipRepository.loadTurips().onSuccess { turips: List<Turip> ->
                _uiState.update { myTuripUiState: MyTuripUiState ->
                    myTuripUiState.copy(
                        turips =
                            turips
                                .map { it.toUiMyTuripModel() }
                                .toImmutableList(),
                    )
                }
            }
        }
    }

    fun showAddBottomSheet() = _uiState.update { it.copy(showAddBottomSheet = true) }

    fun dismissAddBottomSheet() =
        _uiState.update {
            it.copy(
                showAddBottomSheet = false,
                turipNameStatus = TuripNameStatusModel.EMPTY,
                inputTuripName = "",
            )
        }

    fun showTuripRemoveDialog() = _uiState.update { it.copy(showTuripRemoveDialog = true) }

    fun dismissTuripRemoveDialog() = _uiState.update { it.copy(showTuripRemoveDialog = false) }

    fun updateInputName(name: String) {
        val editModels: List<TuripEditModel> =
            _uiState.value.turips.map { it.toEditModel() }
        val status = TuripNameStatusModel.of(name, editModels)
        _uiState.update {
            it.copy(
                inputTuripName = name,
                turipNameStatus = status,
            )
        }
    }

    fun updateDeleteTuripId(id: Long) = _uiState.update { it.copy(deletedTuripId = id) }

    fun addTurip() {
        val name = _uiState.value.inputTuripName
        viewModelScope.launch {
            turipRepository
                .createTurip(name)
                .onSuccess {
                    _uiEffect.send(MyTuripUiEffect.TuripAdded(name))
                    dismissAddBottomSheet()
                    loadTuripFolders()
                }.onFailure {
                    _uiEffect.send(
                        MyTuripUiEffect.ShowError(
                            errorUiState = uiState.value.errorUiState,
                            retryAction = MyTuripRetryAction.AddMyTurip(name),
                        ),
                    )
                }
        }
    }

    fun deleteTurip(folderId: Long) {
        viewModelScope.launch {
            turipRepository
                .deleteTurip(folderId)
                .onSuccess {
                    _uiEffect.send(
                        MyTuripUiEffect.TuripDeleted(
                            uiState.value.turips
                                .find { it.id == folderId }
                                ?.name ?: "",
                        ),
                    )
                    _uiState.update { state: MyTuripUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            turips = state.turips.filter { it.id != folderId }.toImmutableList(),
                        )
                    }
                    Timber.d("튜립 삭제 완료(이름 = )")
                }.onFailure { errorType: ErrorType ->
                    Timber.e("튜립 삭제 실패(이름 = ")
                }
        }
    }

    fun handleErrorRetryRequest(action: MyTuripRetryAction) {
        when (action) {
            is MyTuripRetryAction.UpdateMyTurip -> {
                loadTuripFolders()
            }

            is MyTuripRetryAction.AddMyTurip -> {
                _uiState.update { it.copy(inputTuripName = action.name) }
                addTurip()
            }
        }
    }
}
