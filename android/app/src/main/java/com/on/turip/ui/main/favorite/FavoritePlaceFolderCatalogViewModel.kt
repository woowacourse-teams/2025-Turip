package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.favorite.FavoritePlace
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_ID
import com.on.turip.ui.main.favorite.FavoritePlaceFolderCatalogFragment.Companion.FAVORITE_PLACE_FOLDER_CATALOG_ARGUMENTS_FOLDER_NAME
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoritePlaceFolderCatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoritePlaceRepository: FavoritePlaceRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    private val _favoritePlaceUiState: MutableLiveData<FavoritePlaceUiState> =
        MutableLiveData(FavoritePlaceUiState())
    val favoritePlaceUiState: LiveData<FavoritePlaceUiState> get() = _favoritePlaceUiState

    private val _shareFolder: MutableLiveData<FavoriteFolderShareModel> = MutableLiveData()
    val shareFolder: LiveData<FavoriteFolderShareModel> get() = _shareFolder

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

    init {
        loadPlacesInSelectFolder()
        _favoritePlaceUiState.value = favoritePlaceUiState.value?.copy(folderName = folderName)
    }

    private fun loadPlacesInSelectFolder() {
        viewModelScope.launch {
            favoritePlaceRepository
                .loadFavoritePlaces(folderId)
                .onSuccess { favoritePlaces: List<FavoritePlace> ->
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            places =
                                favoritePlaces
                                    .map { favoritePlace: FavoritePlace ->
                                        favoritePlace.toUiModel()
                                    },
                        )
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(isServerError = false)
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(isNetWorkError = false)
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
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
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            places =
                                favoritePlaceUiState.value?.places?.filter {
                                    it.placeId != placeId
                                } ?: emptyList(),
                        )
                    Timber.d("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 성공")
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(isServerError = false)
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(isNetWorkError = false)
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 실패 (placeId =$placeId)")
                }
        }
    }

    private fun checkError(errorEvent: ErrorEvent) {
        when (errorEvent) {
            ErrorEvent.USER_NOT_HAVE_PERMISSION -> {
                _favoritePlaceUiState.value =
                    favoritePlaceUiState.value?.copy(isServerError = true)
            }

            ErrorEvent.DUPLICATION_FOLDER -> throw IllegalArgumentException("발생할 수 없는 오류")
            ErrorEvent.UNEXPECTED_PROBLEM -> {
                _favoritePlaceUiState.value =
                    favoritePlaceUiState.value?.copy(isServerError = true)
            }

            ErrorEvent.NETWORK_ERROR -> {
                _favoritePlaceUiState.value =
                    favoritePlaceUiState.value?.copy(isServerError = true)
            }

            ErrorEvent.PARSER_ERROR -> {
                _favoritePlaceUiState.value =
                    favoritePlaceUiState.value?.copy(isServerError = true)
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
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            places = newFavoritePlaces,
                        )
                    Timber.d("순서 변경 완료: $newFavoritePlaces")
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("장소 순서 변경 API 호출 실패")
                }
        }
    }

    data class FavoritePlaceUiState(
        val isLoading: Boolean = true,
        val isNetWorkError: Boolean = false,
        val isServerError: Boolean = false,
        val places: List<FavoritePlaceModel> = emptyList(),
        val folderName: String = "",
    )

    fun shareFolder() {
        val shareFolder =
            FavoriteFolderShareModel(
                name = folderName,
                places = favoritePlaceUiState.value?.places?.map { it.toUiModel() } ?: emptyList(),
            )
        _shareFolder.value = shareFolder
    }
}
