package com.on.turip.ui.compose.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.main.favorite.toUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class FolderViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<FolderUiState> =
        MutableStateFlow(FolderUiState.Idle)
    val uiState: StateFlow<FolderUiState> = _uiState.asStateFlow()

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
