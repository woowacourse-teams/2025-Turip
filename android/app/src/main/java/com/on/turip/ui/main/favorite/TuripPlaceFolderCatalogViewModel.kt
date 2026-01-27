package com.on.turip.ui.main.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.favorite.TuripPlace
import com.on.turip.domain.favorite.repository.TuripPlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateTuripPlaceUseCase
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_ID
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_NAME
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.TuripPlaceFolderCatalogRetryAction
import com.on.turip.ui.main.favorite.model.TuripPlaceFolderCatalogUiEffect
import com.on.turip.ui.main.favorite.model.TuripPlaceFolderCatalogUiState
import com.on.turip.ui.main.favorite.model.TuripPlaceModel
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
class TuripPlaceFolderCatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val turipPlaceRepository: TuripPlaceRepository,
    private val updateTuripPlaceUseCase: UpdateTuripPlaceUseCase,
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

    private val _uiState: MutableStateFlow<TuripPlaceFolderCatalogUiState> =
        MutableStateFlow(TuripPlaceFolderCatalogUiState.Idle)
    val uiState: StateFlow<TuripPlaceFolderCatalogUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TuripPlaceFolderCatalogUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TuripPlaceFolderCatalogUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadPlacesInSelectFolder()
    }

    private fun loadPlacesInSelectFolder() {
        viewModelScope.launch {
            turipPlaceRepository
                .loadTuripPlaces(folderId)
                .onSuccess { turipPlaces: List<TuripPlace> ->
                    _uiState.update { state: TuripPlaceFolderCatalogUiState ->
                        state.copy(
                            places = turipPlaces.map { it.toUiModel() },
                            folderName = folderName,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> {
                            handleGlobalError(
                                uiError = uiError,
                                retryAction = TuripPlaceFolderCatalogRetryAction.LoadPlacesInFolder,
                            )
                        }

                        is UiError.Feature -> {
                            Unit
                        }
                    }
                    Timber.e("폴더에 담긴 장소들을 불러오는 API 호출 실패")
                }
        }
    }

    fun updateTuripPlace(
        placeId: Long,
        isTuripPlace: Boolean,
    ) {
        val updatedIsTuripPlace: Boolean = !isTuripPlace
        viewModelScope.launch {
            updateTuripPlaceUseCase(folderId, placeId, updatedIsTuripPlace)
                .onSuccess {
                    _uiState.update { state: TuripPlaceFolderCatalogUiState ->
                        state.copy(places = state.places.filter { it.placeId != placeId })
                    }
                    Timber.d("튜립 장소 목록 바텀 시트, 튜립 장소 상태 업데이트 성공")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> {
                            handleGlobalError(
                                uiError = uiError,
                                retryAction = TuripPlaceFolderCatalogRetryAction.LoadPlacesInFolder,
                            )
                        }

                        is UiError.Feature -> {
                            Unit
                        }
                    }
                    Timber.e("튜립 장소 목록 바텀 시트, 튜립 장소 상태 업데이트 실패 (placeId = $placeId")
                }
        }
    }

    fun updateTuripPlacesOrder(updateTuripPlaces: List<TuripPlaceModel>) {
        viewModelScope.launch {
            turipPlaceRepository
                .updateTuripPlacesOrder(
                    turipId = folderId,
                    updatedOrder = updateTuripPlaces.map { it.turipPlaceId },
                ).onSuccess {
                    _uiState.update { it.copy(places = updateTuripPlaces) }
                    Timber.d("튜립 장소 목록 바텀 시트, 튜립 장소 순서 변경 완료")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> {
                            handleGlobalError(
                                uiError = uiError,
                                retryAction = TuripPlaceFolderCatalogRetryAction.LoadPlacesInFolder,
                            )
                        }

                        is UiError.Feature -> {
                            Unit
                        }
                    }
                    Timber.e("튜립 장소 목록 바텀 시트, 튜립 장소 순서 변경 실패 ")
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
                        TuripPlaceFolderCatalogUiEffect.ShareFolder(favoriteFolderShareModel),
                    )
                }
            }

            UserType.GUEST, UserType.NONE -> {
                viewModelScope.launch {
                    _uiEffect.send(TuripPlaceFolderCatalogUiEffect.ShowFolderShareNotAllowed)
                }
            }
        }
    }

    private suspend fun handleGlobalError(
        uiError: UiError.Global,
        retryAction: TuripPlaceFolderCatalogRetryAction,
    ) {
        when (uiError) {
            UiError.Global.Network -> {
                _uiEffect.send(
                    TuripPlaceFolderCatalogUiEffect.ShowError(ErrorUiState.Network, retryAction),
                )
            }

            UiError.Global.Server -> {
                _uiEffect.send(
                    TuripPlaceFolderCatalogUiEffect.ShowError(ErrorUiState.Server, retryAction),
                )
            }

            UiError.Global.TokenExpired -> {
                _uiEffect.send(TuripPlaceFolderCatalogUiEffect.NavigateToLogin)
            }
        }
    }

    fun handleErrorRetryRequest(action: TuripPlaceFolderCatalogRetryAction) {
        when (action) {
            TuripPlaceFolderCatalogRetryAction.LoadPlacesInFolder -> loadPlacesInSelectFolder()
        }
    }
}
