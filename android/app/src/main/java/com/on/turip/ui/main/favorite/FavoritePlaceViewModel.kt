package com.on.turip.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.onFailure
import com.on.turip.data.result.onSuccess
import com.on.turip.domain.favorite.FavoritePlace
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceUiEffect
import com.on.turip.ui.main.favorite.model.FavoritePlaceUiState
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
class FavoritePlaceViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
    private val favoritePlaceRepository: FavoritePlaceRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<FavoritePlaceUiState> =
        MutableStateFlow(FavoritePlaceUiState.Idle)
    val uiState: StateFlow<FavoritePlaceUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<FavoritePlaceUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<FavoritePlaceUiEffect> = _uiEffect.receiveAsFlow()

    private var selectedFolderId: Long = NOT_INITIALIZED

    fun loadFoldersAndPlaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorUiState = ErrorUiState.None) }
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("장소 찜 목록 화면 폴더 불러오기 성공")
                    ensureValidSelectedFolderId(folders)

                    val loadFolders =
                        folders.map { folder: Folder -> folder.toUiModel(selectFolderId = selectedFolderId) }

                    when (
                        val result: TuripResult<List<FavoritePlace>> =
                            favoritePlaceRepository.loadFavoritePlaces(selectedFolderId)
                    ) {
                        is TuripResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorUiState = ErrorUiState.None,
                                    places = result.value.map { favoritePlace: FavoritePlace -> favoritePlace.toUiModel() },
                                    folders = loadFolders,
                                    placesLatLng = result.value.map { it.toLatLng() },
                                )
                            }
                        }

                        is TuripResult.Failure -> {
                            when (val uiError: UiError = result.errorType.toUiError()) {
                                is UiError.Global -> handleGlobalError(uiError)
                                is UiError.Feature -> Unit
                            }
                            Timber.e("폴더에 담긴 장소들을 불러오는 API 호출 실패")
                        }
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("장소 찜 목록 화면 폴더 불러오기 API 호출 실패")
                }
        }
    }

    private fun ensureValidSelectedFolderId(folders: List<Folder>) {
        if (selectedFolderId == NOT_INITIALIZED || folders.all { it.id != selectedFolderId }) {
            selectedFolderId = folders.firstOrNull { it.name == DEFAULT_FOLDER_NAME }?.id
                ?: folders.firstOrNull()?.id ?: NOT_INITIALIZED
        }
    }

    fun updateFavoritePlace(
        placeId: Long,
        isFavorite: Boolean,
    ) {
        val updatedFavorite: Boolean = !isFavorite
        viewModelScope.launch {
            updateFavoritePlaceUseCase(selectedFolderId, placeId, updatedFavorite)
                .onSuccess {
                    _uiState.update { originUiState: FavoritePlaceUiState ->
                        originUiState.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = originUiState.places.filter { it.placeId != placeId },
                            placesLatLng = originUiState.placesLatLng.filter { it.placeId != placeId },
                        )
                    }
                    Timber.d("찜 목록 화면, 폴더명에 해당하는 찜 장소들 업데이트 성공")
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isLoading = false) }
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(
                                    FavoritePlaceUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Network,
                                        onRetryClick = { updateFavoritePlace(placeId, isFavorite) },
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(
                                    FavoritePlaceUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Server,
                                        onRetryClick = { updateFavoritePlace(placeId, isFavorite) },
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _uiEffect.send(FavoritePlaceUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.e("찜 목록 화면, 폴더명에 해당하는 찜 장소들 업데이트 실패 (placeId =$placeId)")
                }
        }
    }

    fun updateFolderWithPlaces(folderId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorUiState = ErrorUiState.None) }

            favoritePlaceRepository
                .loadFavoritePlaces(folderId)
                .onSuccess { favoritePlaces: List<FavoritePlace> ->
                    selectedFolderId = folderId
                    _uiState.update { originUiState: FavoritePlaceUiState ->
                        originUiState.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = favoritePlaces.map { it.toUiModel() },
                            folders =
                                originUiState.folders.map { folder: FavoritePlaceFolderModel ->
                                    folder.copy(isSelected = folder.id == folderId)
                                },
                            placesLatLng = favoritePlaces.map { it.toLatLng() },
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("폴더에 담긴 장소들을 불러오는 API 호출 실패")
                }
        }
    }

    fun updateFavoritePlacesOrder(newFavoritePlaces: List<FavoritePlaceModel>) {
        viewModelScope.launch {
            favoritePlaceRepository
                .updateFavoritePlacesOrder(
                    favoriteFolderId = selectedFolderId,
                    updatedOrder = newFavoritePlaces.map { it.favoritePlaceId },
                ).onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = newFavoritePlaces,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("장소 순서 변경 API 호출 실패 ")
                }
        }
    }

    fun shareFolder() {
        when (AuthState.type) {
            UserType.MEMBER -> {
                val favoriteFolderShareModel =
                    FavoriteFolderShareModel(
                        name =
                            uiState.value.folders
                                .first { it.isSelected }
                                .name,
                        places = uiState.value.places.map { it.toUiModel() },
                    )
                viewModelScope.launch {
                    _uiEffect.send(FavoritePlaceUiEffect.ShareFolder(favoriteFolderShareModel))
                }
            }

            UserType.GUEST, UserType.NONE -> {
                viewModelScope.launch {
                    _uiEffect.send(FavoritePlaceUiEffect.ShowFolderShareNotAllowed)
                }
            }
        }
    }

    private suspend fun handleGlobalError(uiError: UiError.Global) {
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
                _uiEffect.send(FavoritePlaceUiEffect.NavigateToLogin)
            }
        }
    }

    companion object {
        private const val NOT_INITIALIZED: Long = 0L
        private const val DEFAULT_FOLDER_NAME = "기본 폴더"
    }
}
