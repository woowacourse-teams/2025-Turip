package com.on.turip.ui.main.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.favorite.FavoritePlace
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.compose.favorite.model.DeletePlaceSnapshot
import com.on.turip.ui.compose.favorite.model.FavoritePlaceModel
import com.on.turip.ui.main.favorite.FavoritePlaceFolderBottomSheetFragment.Companion.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID
import com.on.turip.ui.main.favorite.FavoritePlaceFolderBottomSheetFragment.Companion.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_NAME
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderRetryAction
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderScreenMode
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderUiEffect
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
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
class FavoritePlaceFolderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val folderRepository: FolderRepository,
    private val favoritePlaceRepository: FavoritePlaceRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    private val placeId: Long by lazy {
        checkNotNull(savedStateHandle[FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID]) {
            Timber.e("폴더 목록 화면 place ID 값이 존재하지 않습니다.")
        }
    }

    private val placeName: String by lazy {
        checkNotNull(savedStateHandle[FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_NAME]) {
            Timber.e("폴더 목록 화면, 장소명이 존재하지 않습니다.")
        }
    }

    // 폴더 목록의 버튼 활성화 여부를 위한 캐싱
    private var originFavoriteFolderIds: Set<Long> = setOf()

    // 폴더 내 장소들 낙관적 UI를 위한 캐싱
    private var deletePlaceSnapshot: DeletePlaceSnapshot = DeletePlaceSnapshot.EMPTY

    private val _uiState: MutableStateFlow<FavoritePlaceFolderUiState> =
        MutableStateFlow(FavoritePlaceFolderUiState.Idle)
    val uiState: StateFlow<FavoritePlaceFolderUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<FavoritePlaceFolderUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<FavoritePlaceFolderUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadFavoriteFoldersForPlace()
    }

    private fun loadFavoriteFoldersForPlace() {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFoldersStatusByPlaceId(placeId)
                .onSuccess { favoriteFolders: List<FavoriteFolder> ->
                    val folders = favoriteFolders.map { it.toUiModel() }.toImmutableList()

                    _uiState.update { state: FavoritePlaceFolderUiState ->
                        state.copy(
                            screenMode = FavoritePlaceFolderScreenMode.Folders,
                            placeName = placeName,
                            favoritePlaceFolders = folders,
                            selectedFolderPlaces = persistentListOf(),
                            isChanged = false,
                        )
                    }

                    originFavoriteFolderIds = folders.filter { it.isSelected }.map { it.id }.toSet()
                    Timber.d("상세 페이지에서 장소에 대한 찜 폴더 현황 데이터 불러오기 성공 ")
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = FavoritePlaceFolderRetryAction.LoadFavoriteFolders,
                    )
                    Timber.e("상세 페이지에서 장소에 대한 찜 폴더 현황 데이터 불러오기 실패")
                }
        }
    }

    // TODO : UI만 반영하도록 수정, 다음 PR에서 완료 버튼 누르면 전체 변경 내역 반영 API 연동
    fun updateFolder(updateFolder: FavoritePlaceFolderModel) {
        val updatedFavorite: Boolean = !updateFolder.isSelected
        _uiState.update { state ->
            val updateFavoriteFolders =
                state.favoritePlaceFolders
                    .map { folder ->
                        if (folder.id == updateFolder.id) folder.copy(isSelected = updatedFavorite) else folder
                    }.toImmutableList()

            state.copy(
                favoritePlaceFolders = updateFavoriteFolders,
                isChanged = isFavoriteFolderChanged(updateFavoriteFolders),
            )
        }
    }

    private fun isFavoriteFolderChanged(folders: ImmutableList<FavoritePlaceFolderModel>): Boolean {
        val currentFavoriteFolderIds = folders.filter { it.isSelected }.map { it.id }.toSet()
        return originFavoriteFolderIds != currentFavoriteFolderIds
    }

    fun onFavoriteDetailBack() {
        _uiState.update {
            it.copy(
                screenMode = FavoritePlaceFolderScreenMode.Folders,
                selectedFolderPlaces = persistentListOf(),
            )
        }
    }

    fun loadPlacesInSelectFolder(
        folderId: Long,
        folderName: String,
    ) {
        viewModelScope.launch {
            favoritePlaceRepository
                .loadFavoritePlaces(folderId)
                .onSuccess { favoritePlaces: List<FavoritePlace> ->
                    _uiState.update { state: FavoritePlaceFolderUiState ->
                        state.copy(
                            screenMode =
                                FavoritePlaceFolderScreenMode.FolderDetail(folderId, folderName),
                            selectedFolderPlaces =
                                favoritePlaces.map { it.toUiModel() }.toImmutableList(),
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction =
                            FavoritePlaceFolderRetryAction.LoadFavoritePlacesInFolder(
                                folderId = folderId,
                                folderName = folderName,
                            ),
                    )
                    Timber.e("폴더에 담긴 장소들을 불러오는 API 호출 실패")
                }
        }
    }

    // 낙관적 UI / UI 반영만
    fun applyFavoritePlaceDelete(place: FavoritePlaceModel) {
        _uiState.update { state: FavoritePlaceFolderUiState ->
            deletePlaceSnapshot = DeletePlaceSnapshot(place, state.selectedFolderPlaces)
            val updatePlaces =
                state.selectedFolderPlaces
                    .filter { it.favoritePlaceId != place.favoritePlaceId }
                    .toImmutableList()
            state.copy(selectedFolderPlaces = updatePlaces)
        }
        viewModelScope.launch {
            _uiEffect.send(
                FavoritePlaceFolderUiEffect.ShowRemovedFavoritePlace(placeName = place.name),
            )
        }
    }

    // 낙관적 UI / UI 복구
    fun rollbackFavoritePlaceDelete() {
        if (deletePlaceSnapshot.hasSnapshot()) {
            _uiState.update { it.copy(selectedFolderPlaces = deletePlaceSnapshot.originPlaces) }
            deletePlaceSnapshot = DeletePlaceSnapshot.EMPTY
        }
    }

    // 낙관적 UI / API 호출 & 조건에 따라 폴더 목록 화면 데이터 동기화
    fun commitFavoritePlaceDelete() {
        if (!deletePlaceSnapshot.hasSnapshot()) {
            Timber.e("제거할 장소에 대한 정보가 없어요. favoritePlacesSnapshot을 확인 해주세요 ")
            return
        }

        val deletePlace = deletePlaceSnapshot.deletePlace
        if (uiState.value.screenMode is FavoritePlaceFolderScreenMode.FolderDetail) {
            val screenMode = uiState.value.screenMode as FavoritePlaceFolderScreenMode.FolderDetail
            viewModelScope.launch {
                updateFavoritePlaceUseCase(screenMode.folderId, deletePlace.placeId, false)
                    .onSuccess {
                        syncFolderForSelectedPlace(deletePlace, screenMode)
                        Timber.d("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 성공")
                    }.onFailure {
                        _uiEffect.send(FavoritePlaceFolderUiEffect.DeletePlaceFailed)
                        _uiState.update { it.copy(selectedFolderPlaces = deletePlaceSnapshot.originPlaces) }
                        Timber.e("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 실패 (placeId = $placeId)")
                    }

                deletePlaceSnapshot = DeletePlaceSnapshot.EMPTY
            }
        }
    }

    // 바텀 시트 진입할 때 선택한 장소와 폴더의 장소가 동일한 경우 데이터 동기화
    private fun syncFolderForSelectedPlace(
        deletePlace: FavoritePlaceModel,
        screenMode: FavoritePlaceFolderScreenMode.FolderDetail,
    ) {
        if (placeId == deletePlace.placeId) {
            _uiState.update { state ->
                val syncFolderStatus =
                    state.favoritePlaceFolders
                        .map { if (it.id == screenMode.folderId) it.copy(isSelected = false) else it }
                        .toImmutableList()

                state.copy(favoritePlaceFolders = syncFolderStatus)
            }
            val updateCache =
                originFavoriteFolderIds
                    .toMutableSet()
                    .apply { remove(screenMode.folderId) }
                    .toSet()
            originFavoriteFolderIds = updateCache
        }
    }

    fun shareFolder() {
        if (uiState.value.screenMode is FavoritePlaceFolderScreenMode.FolderDetail) {
            val screenMode = uiState.value.screenMode as FavoritePlaceFolderScreenMode.FolderDetail

            when (AuthState.type) {
                UserType.MEMBER -> {
                    val favoriteFolderShareModel =
                        FavoriteFolderShareModel(
                            name = screenMode.folderName,
                            places =
                                uiState.value.selectedFolderPlaces
                                    .map { it.toUiModel() }
                                    .toImmutableList(),
                        )
                    viewModelScope.launch {
                        _uiEffect.send(
                            FavoritePlaceFolderUiEffect.ShareFolder(favoriteFolderShareModel),
                        )
                    }
                }

                UserType.GUEST, UserType.NONE -> {
                    viewModelScope.launch {
                        _uiEffect.send(FavoritePlaceFolderUiEffect.FolderShareNotAllowed)
                    }
                }
            }
        }
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: FavoritePlaceFolderRetryAction,
    ) {
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiEffect.send(
                        FavoritePlaceFolderUiEffect.ShowError(ErrorUiState.Network, retryAction),
                    )
                }

                UiError.Global.Server -> {
                    _uiEffect.send(
                        FavoritePlaceFolderUiEffect.ShowError(ErrorUiState.Server, retryAction),
                    )
                }

                UiError.Global.TokenExpired -> {
                    _uiEffect.send(FavoritePlaceFolderUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun handleErrorRetryRequest(action: FavoritePlaceFolderRetryAction) {
        when (action) {
            FavoritePlaceFolderRetryAction.LoadFavoriteFolders -> {
                loadFavoriteFoldersForPlace()
            }

            is FavoritePlaceFolderRetryAction.UpdateFolder -> {
                updateFolder(action.favoritePlaceFolderModel)
            }

            is FavoritePlaceFolderRetryAction.LoadFavoritePlacesInFolder -> {
                loadPlacesInSelectFolder(
                    folderId = action.folderId,
                    folderName = action.folderName,
                )
            }
        }
    }
}
