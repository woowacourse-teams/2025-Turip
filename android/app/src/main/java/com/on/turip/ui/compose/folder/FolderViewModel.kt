package com.on.turip.ui.compose.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.compose.folder.mapper.toUiModel
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

    fun handleErrorRetryRequest(action: FolderRetryAction) {
        when (action) {
            is FolderRetryAction.UpdateFolder -> {
                loadTuripFolders()
            }
        }
    }

    fun loadTuripFolders() {
        viewModelScope.launch {
            turipRepository.loadTurips().onSuccess { turips: List<Turip> ->
                _uiState.update { folderUiState: FolderUiState ->
                    folderUiState.copy(turips = turips.map { it.toUiModel() }.toImmutableList())
                }
            }
        }
    }
}
