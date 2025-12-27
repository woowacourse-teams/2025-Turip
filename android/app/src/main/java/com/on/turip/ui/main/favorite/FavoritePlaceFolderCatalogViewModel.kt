package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.on.turip.data.common.onFailureWithCause
import com.on.turip.data.common.onSuccess
import com.on.turip.data.common.toUiError
import com.on.turip.domain.favorite.FavoritePlace
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_ID
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_NAME
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoritePlaceFolderCatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoritePlaceRepository: FavoritePlaceRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    private val _favoritePlaceFolderCatalogUiState: MutableLiveData<FavoritePlaceFolderCatalogUiState> =
        MutableLiveData(FavoritePlaceFolderCatalogUiState())
    val favoritePlaceFolderCatalogUiState: LiveData<FavoritePlaceFolderCatalogUiState> get() = _favoritePlaceFolderCatalogUiState

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
        _favoritePlaceFolderCatalogUiState.value =
            favoritePlaceFolderCatalogUiState.value?.copy(folderName = folderName)

        viewModelScope.launch {
            favoritePlaceRepository
                .loadFavoritePlaces(folderId)
                .onSuccess { favoritePlaces: List<FavoritePlace> ->
                    _favoritePlaceFolderCatalogUiState.value =
                        favoritePlaceFolderCatalogUiState.value?.copy(
                            places = favoritePlaces.map { favoritePlace: FavoritePlace -> favoritePlace.toUiModel() },
                        )
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("폴더에 담긴 장소들을 불러오는 API 호출 실패 : $errorType / $cause")
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
                    _favoritePlaceFolderCatalogUiState.value =
                        favoritePlaceFolderCatalogUiState.value?.copy(
                            places =
                                favoritePlaceFolderCatalogUiState.value?.places?.filter { it.placeId != placeId }
                                    ?: emptyList(),
                        )
                    Timber.d("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 성공")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 실패 (placeId = $placeId) : $errorType / $cause")
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
                    _favoritePlaceFolderCatalogUiState.value =
                        favoritePlaceFolderCatalogUiState.value?.copy(places = newFavoritePlaces)
                    Timber.d("순서 변경 완료: $newFavoritePlaces")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("장소 순서 변경 API 호출 실패 : $errorType / $cause")
                }
        }
    }

    fun shareFolder() {
        when (AuthState.type) {
            UserType.MEMBER -> {
                val favoriteFolderShareModel =
                    FavoriteFolderShareModel(
                        name = folderName,
                        places =
                            favoritePlaceFolderCatalogUiState.value?.places?.map { it.toUiModel() }
                                ?: emptyList(),
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

    sealed interface FavoritePlaceFolderCatalogUiEffect {
        data object ShowFolderShareNotAllowed : FavoritePlaceFolderCatalogUiEffect

        data class ShareFolder(
            val favoriteFolderShareModel: FavoriteFolderShareModel,
        ) : FavoritePlaceFolderCatalogUiEffect
    }

    data class FavoritePlaceFolderCatalogUiState(
        val places: List<FavoritePlaceModel> = emptyList(),
        val folderName: String = "",
    )
}
