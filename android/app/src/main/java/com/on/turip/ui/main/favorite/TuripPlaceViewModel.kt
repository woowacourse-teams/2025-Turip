package com.on.turip.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.favorite.TuripPlace
import com.on.turip.domain.favorite.repository.TuripPlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateTuripPlaceUseCase
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.TuripPlaceRetryAction
import com.on.turip.ui.main.favorite.model.TuripPlaceUiEffect
import com.on.turip.ui.main.favorite.model.TuripPlaceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TuripPlaceViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
    private val turipPlaceRepository: TuripPlaceRepository,
    private val updateTuripPlaceUseCase: UpdateTuripPlaceUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TuripPlaceUiState> =
        MutableStateFlow(TuripPlaceUiState.Idle)
    val uiState: StateFlow<TuripPlaceUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TuripPlaceUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TuripPlaceUiEffect> = _uiEffect.receiveAsFlow()

    private var selectedFolderId: Long = NOT_INITIALIZED

    fun loadFoldersAndPlaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("튜립 불러오기 성공")
                    ensureValidSelectedFolderId(folders)

                    val loadFolders =
                        folders.map { folder: Folder -> folder.toUiModel(selectFolderId = selectedFolderId) }

                    when (
                        val result: TuripResult<List<TuripPlace>> =
                            turipPlaceRepository.loadTuripPlaces(selectedFolderId)
                    ) {
                        is TuripResult.Success -> {
                            _uiState.update { state: TuripPlaceUiState ->
                                state.copy(
                                    isLoading = false,
                                    errorUiState = ErrorUiState.None,
                                    places = result.value.map { turipPlace: TuripPlace -> turipPlace.toUiModel() },
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
                            Timber.e("튜립 장소 목록 조회 API 호출 실패")
                        }
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("튜립 목록 조회 API 호출 실패")
                }
        }
    }

    private fun ensureValidSelectedFolderId(folders: List<Folder>) {
        if (selectedFolderId == NOT_INITIALIZED || folders.all { it.id != selectedFolderId }) {
            selectedFolderId = folders.firstOrNull { it.name == DEFAULT_FOLDER_NAME }?.id
                ?: folders.firstOrNull()?.id ?: NOT_INITIALIZED
        }
    }

    fun updateTuripPlace(
        placeId: Long,
        isTuripPlace: Boolean,
    ) {
        val updatedIsTuripPlace: Boolean = !isTuripPlace
        viewModelScope.launch {
            updateTuripPlaceUseCase(selectedFolderId, placeId, updatedIsTuripPlace)
                .onSuccess {
                    _uiState.update { state: TuripPlaceUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = state.places.filter { it.placeId != placeId },
                            placesLatLng = state.placesLatLng.filter { it.placeId != placeId },
                        )
                    }
                    Timber.d("튜립 내 튜립 장소 상태 업데이트 성공, turipId = $selectedFolderId placeId = $placeId")
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isLoading = false) }
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(
                                    TuripPlaceUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Network,
                                        retryAction =
                                            TuripPlaceRetryAction
                                                .UpdateTuripPlace(placeId, isTuripPlace),
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(
                                    TuripPlaceUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Server,
                                        retryAction =
                                            TuripPlaceRetryAction
                                                .UpdateTuripPlace(placeId, isTuripPlace),
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _uiEffect.send(TuripPlaceUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.d("튜립 내 튜립 장소 상태 업데이트 실패, turipId = $selectedFolderId placeId = $placeId originIsTuripPlace =$isTuripPlace")
                }
        }
    }

    fun updateFolderWithPlaces(folderId: Long) {
        if (folderId == selectedFolderId) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            turipPlaceRepository
                .loadTuripPlaces(folderId)
                .onSuccess { turipPlaces: List<TuripPlace> ->
                    selectedFolderId = folderId
                    _uiState.update { state: TuripPlaceUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = turipPlaces.map { it.toUiModel() },
                            folders =
                                state.folders.map { folder: FavoritePlaceFolderModel ->
                                    folder.copy(isSelected = folder.id == folderId)
                                },
                            placesLatLng = turipPlaces.map { it.toLatLng() },
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("튜립에 포함된 장소 불러오는 API 호출 실패 turipId =$folderId")
                }
        }
    }

    fun updateTuripPlacesOrder(updateTuripPlaces: List<TuripPlaceModel>) {
        viewModelScope.launch {
            turipPlaceRepository
                .updateTuripPlacesOrder(
                    turipId = selectedFolderId,
                    updatedOrder = updateTuripPlaces.map { it.turipPlaceId },
                ).onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = updateTuripPlaces,
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
                    _uiEffect.send(TuripPlaceUiEffect.ShareFolder(favoriteFolderShareModel))
                }
            }

            UserType.GUEST, UserType.NONE -> {
                viewModelScope.launch {
                    _uiEffect.send(TuripPlaceUiEffect.ShowFolderShareNotAllowed)
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
                _uiEffect.send(TuripPlaceUiEffect.NavigateToLogin)
            }
        }
    }

    fun handleErrorRetryRequest(action: TuripPlaceRetryAction) {
        when (action) {
            is TuripPlaceRetryAction.UpdateTuripPlace -> {
                updateTuripPlace(action.placeId, action.isTuripPlace)
            }
        }
    }

    companion object {
        private const val NOT_INITIALIZED: Long = 0L
        private const val DEFAULT_FOLDER_NAME = "기본 폴더"
    }
}
