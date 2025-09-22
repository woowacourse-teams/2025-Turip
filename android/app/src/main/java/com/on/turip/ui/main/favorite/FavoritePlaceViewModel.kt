package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.favorite.FavoritePlace
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoritePlaceViewModel(
    private val folderRepository: FolderRepository,
    private val favoritePlaceRepository: FavoritePlaceRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    private val _favoritePlaceUiState: MutableLiveData<FavoritePlaceUiState> =
        MutableLiveData(FavoritePlaceUiState())
    val favoritePlaceUiState: LiveData<FavoritePlaceUiState> get() = _favoritePlaceUiState

    var selectedFolderId: Long = NOT_INITIALIZED

    fun loadFoldersAndPlaces() {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("장소 찜 목록 화면 폴더 불러오기 성공")
                    if (selectedFolderId == NOT_INITIALIZED) selectedFolderId = folders[0].id
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            folders =
                                folders.map { folder: Folder ->
                                    folder.toUiModel(selectedFolderId)
                                },
                        )
                    loadPlacesInSelectFolder()
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(isServerError = false)
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(isNetWorkError = false)
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("장소 찜 목록 화면 폴더 불러오기 API 호출 실패")
                }
        }
    }

    private suspend fun loadPlacesInSelectFolder() {
        favoritePlaceRepository
            .loadFavoritePlaces(selectedFolderId)
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

    fun updateFavoritePlace(
        placeId: Long,
        isFavorite: Boolean,
    ) {
        val updatedFavorite: Boolean = !isFavorite
        viewModelScope.launch {
            updateFavoritePlaceUseCase(selectedFolderId, placeId, updatedFavorite)
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

    fun updateFolderWithPlaces(folderId: Long) {
        val isDeleted: Boolean =
            favoritePlaceUiState.value?.folders?.all { it.id != folderId } ?: true
        selectedFolderId =
            if (isDeleted) {
                favoritePlaceUiState.value
                    ?.folders
                    ?.get(0)
                    ?.id
                    ?: NOT_INITIALIZED
            } else {
                folderId
            }

        _favoritePlaceUiState.value =
            favoritePlaceUiState.value?.copy(
                folders =
                    favoritePlaceUiState.value?.folders?.map { favoriteFolder: FavoritePlaceFolderModel ->
                        favoriteFolder.copy(isSelected = favoriteFolder.id == selectedFolderId)
                    } ?: emptyList(),
            )
        viewModelScope.launch {
            favoritePlaceRepository
                .loadFavoritePlaces(folderId)
                .onSuccess { favoritePlaces: List<FavoritePlace> ->
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            places = favoritePlaces.map { it.toUiModel() },
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
                    favoriteFolderId = selectedFolderId,
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
        val folders: List<FavoritePlaceFolderModel> = emptyList(),
    )

    companion object {
        private const val NOT_INITIALIZED: Long = 0L

        fun provideFactory(
            folderRepository: FolderRepository = RepositoryModule.folderRepository,
            favoritePlaceRepository: FavoritePlaceRepository = RepositoryModule.favoritePlaceRepository,
            updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase =
                UpdateFavoritePlaceUseCase(
                    favoritePlaceRepository,
                ),
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FavoritePlaceViewModel(
                        folderRepository,
                        favoritePlaceRepository,
                        updateFavoritePlaceUseCase,
                    )
                }
            }
    }
}
