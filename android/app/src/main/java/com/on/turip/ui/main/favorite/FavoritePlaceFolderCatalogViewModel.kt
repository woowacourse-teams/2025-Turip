package com.on.turip.ui.main.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.data.common.ErrorType
import com.on.turip.data.common.ErrorUiEffect
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.UiError
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.data.common.toUiError
import com.on.turip.domain.favorite.FavoritePlace
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_ID
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_NAME
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderCatalogUiEffect
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderCatalogUiState
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
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
class FavoritePlaceFolderCatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoritePlaceRepository: FavoritePlaceRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    private val folderId: Long by lazy {
        checkNotNull(savedStateHandle[FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_ID]) {
            Timber.e("찜 폴더 내 장소 목록 화면 Folder ID 값이 존재하지 않습니다.")
        }
    }
    private val folderName: String by lazy {
        checkNotNull(savedStateHandle[FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_NAME]) {
            Timber.e("찜 폴더 내 장소 목록 화면 폴더 이름이 존재하지 않습니다.")
        }
    }

    private val _uiState: MutableStateFlow<FavoritePlaceFolderCatalogUiState> =
        MutableStateFlow(FavoritePlaceFolderCatalogUiState.Idle)
    val uiState: StateFlow<FavoritePlaceFolderCatalogUiState> = _uiState.asStateFlow()

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    private val _uiEffect: Channel<FavoritePlaceFolderCatalogUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<FavoritePlaceFolderCatalogUiEffect> = _uiEffect.receiveAsFlow()

    private val _errorUiEffect: Channel<ErrorUiEffect> = Channel(Channel.BUFFERED)
    val errorUiEffect: Flow<ErrorUiEffect> = _errorUiEffect.receiveAsFlow()

    init {
        loadPlacesInSelectFolder()
    }

    private fun loadPlacesInSelectFolder() {
        viewModelScope.launch {
            favoritePlaceRepository
                .loadFavoritePlaces(folderId)
                .onSuccess { favoritePlaces: List<FavoritePlace> ->
                    _uiState.update {
                        it.copy(
                            places = favoritePlaces.map { it.toUiModel() },
                            folderName = folderName,
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

    fun updateFavoritePlace(
        placeId: Long,
        isFavorite: Boolean,
    ) {
        val updatedFavorite: Boolean = !isFavorite
        viewModelScope.launch {
            updateFavoritePlaceUseCase(folderId, placeId, updatedFavorite)
                .onSuccess {
                    _uiState.update { originUiState: FavoritePlaceFolderCatalogUiState ->
                        originUiState.copy(
                            places = originUiState.places.filter { it.placeId != placeId },
                        )
                    }
                    Timber.d("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 성공")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 실패 (placeId = $placeId)")
                }
        }
    }

    fun updateFavoritePlacesOrder(newFavoritePlaces: List<FavoritePlaceModel>) {
        viewModelScope.launch {
            favoritePlaceRepository
                .updateFavoritePlacesOrder(
                    favoriteFolderId = folderId,
                    updatedOrder = newFavoritePlaces.map { it.favoritePlaceId },
                ).onSuccess {
                    _uiState.update { it.copy(places = newFavoritePlaces) }
                    Timber.d("순서 변경 완료: $newFavoritePlaces")
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
                        name = folderName,
                        places = uiState.value.places.map { it.toUiModel() },
                    )
                viewModelScope.launch {
                    _uiEffect.send(
                        FavoritePlaceFolderCatalogUiEffect.ShareFolder(favoriteFolderShareModel),
                    )
                }
            }

            UserType.GUEST, UserType.NONE -> {
                viewModelScope.launch {
                    _uiEffect.send(FavoritePlaceFolderCatalogUiEffect.ShowFolderShareNotAllowed)
                }
            }
        }
    }

    private suspend fun handleGlobalError(uiError: UiError.Global) {
        when (uiError) {
            UiError.Global.Network -> {
                _errorUiEffect.send(
                    ErrorUiEffect.ShowSnackbar(
                        errorUiState = ErrorUiState.Network,
                        onRetryClick = { loadPlacesInSelectFolder() },
                    ),
                )
            }

            UiError.Global.Server -> {
                _errorUiEffect.send(
                    ErrorUiEffect.ShowSnackbar(
                        errorUiState = ErrorUiState.Server,
                        onRetryClick = { loadPlacesInSelectFolder() },
                    ),
                )
            }

            UiError.Global.TokenExpired -> {
                _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
            }
        }
    }
}
