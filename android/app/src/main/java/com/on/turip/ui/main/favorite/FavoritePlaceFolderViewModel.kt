package com.on.turip.ui.main.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.data.result.onFailure
import com.on.turip.data.result.onSuccess
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.main.favorite.FavoritePlaceFolderFragment.Companion.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderUiEffect
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
class FavoritePlaceFolderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val folderRepository: FolderRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    private val placeId: Long by lazy {
        checkNotNull(savedStateHandle[FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID]) {
            Timber.e("폴더 목록 화면 place ID 값이 존재하지 않습니다.")
        }
    }

    private val _uiState: MutableStateFlow<FavoritePlaceFolderUiState> =
        MutableStateFlow(FavoritePlaceFolderUiState.Idle)
    val uiState: StateFlow<FavoritePlaceFolderUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<FavoritePlaceFolderUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<FavoritePlaceFolderUiEffect> = _uiEffect.receiveAsFlow()

    fun loadFavoriteFoldersForPlace() {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFoldersStatusByPlaceId(placeId)
                .onSuccess { favoriteFolders: List<FavoriteFolder> ->
                    _uiState.update { state: FavoritePlaceFolderUiState ->
                        state.copy(
                            placeId = placeId,
                            favoritePlaceFolders = favoriteFolders.map { it.toUiModel() },
                        )
                    }
                    Timber.d("상세 페이지에서 장소에 대한 찜 폴더 현황 데이터 불러오기 성공 ")
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(
                                    FavoritePlaceFolderUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Network,
                                        onRetryClick = { loadFavoriteFoldersForPlace() },
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(
                                    FavoritePlaceFolderUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Server,
                                        onRetryClick = { loadFavoriteFoldersForPlace() },
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _uiEffect.send(FavoritePlaceFolderUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.e("상세 페이지에서 장소에 대한 찜 폴더 현황 데이터 불러오기 실패")
                }
        }
    }

    fun updateFolder(favoritePlaceFolderModel: FavoritePlaceFolderModel) {
        viewModelScope.launch {
            val updateFavoritesStatus: Boolean = !favoritePlaceFolderModel.isSelected
            updateFavoritePlaceUseCase(favoritePlaceFolderModel.id, placeId, updateFavoritesStatus)
                .onSuccess {
                    Timber.d("장소에 대한 찜 폴더들 현황에서 장소 찜 업데이트")
                    _uiState.update { state: FavoritePlaceFolderUiState ->
                        state.copy(
                            favoritePlaceFolders =
                                state.favoritePlaceFolders.map { folder ->
                                    if (folder.id == favoritePlaceFolderModel.id) {
                                        folder.copy(isSelected = !folder.isSelected)
                                    } else {
                                        folder
                                    }
                                },
                        )
                    }
                    _uiEffect.send(
                        FavoritePlaceFolderUiEffect.ShowUpdateFavoriteState(favoritePlaceFolderModel),
                    )
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(
                                    FavoritePlaceFolderUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Network,
                                        onRetryClick = { updateFolder(favoritePlaceFolderModel) },
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(
                                    FavoritePlaceFolderUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Server,
                                        onRetryClick = { updateFolder(favoritePlaceFolderModel) },
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _uiEffect.send(FavoritePlaceFolderUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.e("장소에 대한 찜 폴더들 현황에서 장소 찜 실패")
                }
        }
    }
}
