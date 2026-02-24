package com.on.turip.ui.compose.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.folder.mapper.toUiModel
import com.on.turip.ui.folder.model.TuripEditModel
import com.on.turip.ui.folder.model.TuripNameStatusModel
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
class FolderViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<FolderUiState> =
        MutableStateFlow(FolderUiState.Idle)
    val uiState: StateFlow<FolderUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<FolderUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<FolderUiEffect> = _uiEffect.receiveAsFlow()

    fun loadTuripFolders() {
        viewModelScope.launch {
            turipRepository.loadTurips().onSuccess { turips: List<Turip> ->
                _uiState.update { folderUiState: FolderUiState ->
                    folderUiState.copy(turips = turips.map { it.toUiModel() }.toImmutableList())
                }
            }
        }
    }

    fun showAddBottomSheet() {
        _uiState.update { it.copy(showAddBottomSheet = true) }
    }

    fun dismissAddBottomSheet() {
        _uiState.update {
            it.copy(
                showAddBottomSheet = false,
                turipNameStatus = TuripNameStatusModel.EMPTY,
                currentTuripName = "",
            )
        }
    }

    fun updateTuripName(name: String) {
        val editModels: List<TuripEditModel> =
            _uiState.value.turips.map { TuripEditModel(it.id, it.name, 2) }
        val status = TuripNameStatusModel.of(name, editModels)
        _uiState.update {
            it.copy(
                currentTuripName = name,
                turipNameStatus = status,
            )
        }
    }

    fun updateDeleteTuripId(id: Long) {
        _uiState.update {
            it.copy(
                deletedTuripId = id,
            )
        }
    }

    fun addTurip() {
        val name = _uiState.value.currentTuripName
        viewModelScope.launch {
            turipRepository
                .createTurip(name)
                .onSuccess {
                    _uiEffect.send(FolderUiEffect.TuripAdded(name))
                    dismissAddBottomSheet()
                    loadTuripFolders()
                }.onFailure {
                    _uiEffect.send(
                        FolderUiEffect.ShowError(
                            errorUiState = uiState.value.errorUiState,
                            retryAction = FolderRetryAction.AddFolder(name),
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
                        FolderUiEffect.TuripDeleted(
                            uiState.value.turips
                                .find { it.id == folderId }
                                ?.name ?: "",
                        ),
                    )
                    _uiState.update { state: FolderUiState ->
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

    fun handleErrorRetryRequest(action: FolderRetryAction) {
        when (action) {
            is FolderRetryAction.UpdateFolder -> {
                loadTuripFolders()
            }

            is FolderRetryAction.AddFolder -> {
                _uiState.update { it.copy(currentTuripName = action.name) }
                addTurip()
            }
        }
    }
}
