package com.on.turip.ui.main.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.main.favorite.FavoritePlaceFolderBottomSheetFragment.Companion.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID
import com.on.turip.ui.main.favorite.FavoritePlaceFolderBottomSheetFragment.Companion.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_NAME
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderRetryAction
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderUiEffect
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
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

    private var originFavoriteFolderIds: Set<Long> = setOf()

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
                            placeId = placeId,
                            placeName = placeName,
                            favoritePlaceFolders = folders,
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
            FavoritePlaceFolderRetryAction.LoadFavoriteFolders -> loadFavoriteFoldersForPlace()
            is FavoritePlaceFolderRetryAction.UpdateFolder -> updateFolder(action.favoritePlaceFolderModel)
        }
    }

    private fun isFavoriteFolderChanged(folders: ImmutableList<FavoritePlaceFolderModel>): Boolean {
        val currentFavoriteFolderIds = folders.filter { it.isSelected }.map { it.id }.toSet()
        return originFavoriteFolderIds != currentFavoriteFolderIds
    }
}
