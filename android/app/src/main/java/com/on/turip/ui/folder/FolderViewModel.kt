package com.on.turip.ui.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.ErrorType
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.UiError
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.data.common.toUiError
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.mapper.toEditUiModel
import com.on.turip.ui.folder.model.FolderEditModel
import com.on.turip.ui.folder.model.FolderNameStatusModel
import com.on.turip.ui.folder.model.FolderUiEffect
import com.on.turip.ui.folder.model.FolderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<FolderUiState> = MutableStateFlow(FolderUiState.Idle)
    val uiState: StateFlow<FolderUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<FolderUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<FolderUiEffect> = _uiEffect.receiveAsFlow()

    private val inputFolderName: MutableStateFlow<String> = MutableStateFlow("")

    val folderNameStatus: StateFlow<FolderNameStatusModel> =
        combine(inputFolderName, uiState) { name, uiState ->
            FolderNameStatusModel.of(name, uiState.folders)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = FolderNameStatusModel.EMPTY,
        )

    fun getSelectedFolderOrNull(): FolderEditModel? =
        uiState.value.folders.firstOrNull { it.isSelected }

    init {
        loadFolders()
    }

    fun loadFolders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorUiState = ErrorUiState.None) }
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("찜 폴더 목록 설정 화면 폴더 불러오기 성공")
                    _uiState.update { state: FolderUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            folders =
                                folders
                                    .filter { !it.isDefault }
                                    .map { folder -> folder.toEditUiModel() },
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
                                _uiEffect.send(FolderUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.e("찜 폴더 목록 설정 화면 폴더 불러오기 API 호출 실패")
                }
        }
    }

    fun updateInputFolderName(input: String) {
        inputFolderName.update { input }
    }

    fun addFolder() {
        viewModelScope.launch {
            folderRepository
                .createFavoriteFolder(inputFolderName.value)
                .onSuccess {
                    Timber.d("폴더 생성 완료(폴더명 = ${inputFolderName.value})")
                    _uiState.update { originUiState: FolderUiState ->
                        originUiState.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            folders = originUiState.folders.plus(it.toEditUiModel()),
                        )
                    }
                    inputFolderName.update { "" }
                    _uiEffect.send(FolderUiEffect.FolderAdded)
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, { addFolder() })
                    Timber.e("폴더 생성 실패")
                }
        }
    }

    fun selectFolder(folderId: Long) {
        _uiState.update { originUiState: FolderUiState ->
            originUiState.copy(folders = originUiState.folders.map { it.copy(isSelected = it.id == folderId) })
        }
    }

    fun updateFolderName() {
        val selectFolder: FolderEditModel = getSelectedFolderOrNull() ?: return
        viewModelScope.launch {
            folderRepository
                .updateFavoriteFolder(selectFolder.id, inputFolderName.value)
                .onSuccess {
                    Timber.d("폴더 수정 완료(폴더명 = ${selectFolder.name} -> ${inputFolderName.value})")
                    _uiState.update { originState: FolderUiState ->
                        originState.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            folders =
                                originState.folders.map { folder: FolderEditModel ->
                                    if (folder.id == selectFolder.id) {
                                        folder.copy(name = inputFolderName.value)
                                    } else {
                                        folder
                                    }
                                },
                        )
                    }
                    inputFolderName.update { "" }
                    _uiEffect.send(FolderUiEffect.FolderUpdated)
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, { updateFolderName() })
                    Timber.e("폴더 수정 실패")
                }
        }
    }

    fun deleteFolder() {
        val selectFolder: FolderEditModel = getSelectedFolderOrNull() ?: return
        viewModelScope.launch {
            folderRepository
                .deleteFavoriteFolder(selectFolder.id)
                .onSuccess {
                    _uiState.update { originUiState: FolderUiState ->
                        originUiState.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            folders = originUiState.folders.filter { it.id != selectFolder.id },
                        )
                    }
                    Timber.d("폴더 삭제 완료(폴더명 = ${selectFolder.name})")
                    _uiEffect.send(FolderUiEffect.FolderDeleted)
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(errorType, { deleteFolder() })
                    Timber.d("폴더 삭제 실패")
                }
        }
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        onRetryClick: (() -> Unit),
    ) {
        _uiState.update { it.copy(isLoading = false, errorUiState = ErrorUiState.None) }
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiEffect.send(
                        FolderUiEffect.ShowError(
                            errorUiState = ErrorUiState.Network,
                            onRetryClick = onRetryClick,
                        ),
                    )
                }

                UiError.Global.Server -> {
                    _uiEffect.send(
                        FolderUiEffect.ShowError(
                            errorUiState = ErrorUiState.Server,
                            onRetryClick = onRetryClick,
                        ),
                    )
                }

                UiError.Global.TokenExpired -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.send(FolderUiEffect.NavigateToLogin)
                }
            }
        }
    }
}
