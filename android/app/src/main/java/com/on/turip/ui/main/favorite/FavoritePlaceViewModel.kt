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
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
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

    private val _shareFolder: MutableLiveData<FavoriteFolderShareModel> = MutableLiveData()
    val shareFolder: LiveData<FavoriteFolderShareModel> get() = _shareFolder

    private var selectedFolderId: Long = NOT_INITIALIZED

    fun loadFoldersAndPlaces() {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("장소 찜 목록 화면 폴더 불러오기 성공")
                    ensureValidSelectedFolderId(folders)

                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            folders =
                                folders.map { folder: Folder -> folder.toUiModel(selectFolderId = selectedFolderId) },
                        )
                    loadPlacesInSelectFolder()
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            isServerError = false,
                            isNetWorkError = false,
                        )
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
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

    private suspend fun loadPlacesInSelectFolder() {
        favoritePlaceRepository
            .loadFavoritePlaces(selectedFolderId)
            .onSuccess { favoritePlaces: List<FavoritePlace> ->
                _favoritePlaceUiState.value =
                    favoritePlaceUiState.value?.copy(
                        places =
                            favoritePlaces.map { favoritePlace: FavoritePlace -> favoritePlace.toUiModel() },
                    )
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
                                favoritePlaceUiState.value?.places?.filter { it.placeId != placeId }
                                    ?: emptyList(),
                        )
                    Timber.d("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 성공")
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            isServerError = false,
                            isNetWorkError = false,
                        )
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 실패 (placeId =$placeId)")
                }
        }
    }

    fun updateFolderWithPlaces(folderId: Long) {
        selectedFolderId = folderId

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
                        favoritePlaceUiState.value?.copy(places = favoritePlaces.map { it.toUiModel() })
                    _favoritePlaceUiState.value =
                        favoritePlaceUiState.value?.copy(
                            isServerError = false,
                            isNetWorkError = false,
                        )
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("폴더에 담긴 장소들을 불러오는 API 호출 실패")
                }
        }
    }

    private fun checkError(errorEvent: ErrorEvent) {
        when (errorEvent) {
            ErrorEvent.USER_NOT_HAVE_PERMISSION -> {
                _favoritePlaceUiState.value = favoritePlaceUiState.value?.copy(isServerError = true)
            }

            ErrorEvent.DUPLICATION_FOLDER -> throw IllegalArgumentException("발생할 수 없는 오류")
            ErrorEvent.UNEXPECTED_PROBLEM -> {
                _favoritePlaceUiState.value = favoritePlaceUiState.value?.copy(isServerError = true)
            }

            ErrorEvent.NETWORK_ERROR -> {
                _favoritePlaceUiState.value = favoritePlaceUiState.value?.copy(isServerError = true)
            }

            ErrorEvent.PARSER_ERROR -> {
                _favoritePlaceUiState.value = favoritePlaceUiState.value?.copy(isServerError = true)
            }
        }
    }

    fun updateFavoritePlacesOrder(newFavoritePlaces: List<FavoritePlaceModel>) {
        _favoritePlaceUiState.value =
            favoritePlaceUiState.value?.copy(
                places = newFavoritePlaces,
            )
        viewModelScope.launch {
            favoritePlaceRepository
                .updateFavoritePlacesOrder(
                    favoriteFolderId = selectedFolderId,
                    updatedOrder = newFavoritePlaces.map { it.favoritePlaceId },
                ).onSuccess {
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

    fun shareFolder() {
        val shareFolder =
            FavoriteFolderShareModel(
                name =
                    favoritePlaceUiState.value
                        ?.folders
                        ?.first { it.isSelected }
                        ?.name ?: "",
                places = favoritePlaceUiState.value?.places?.map { it.toUiModel() } ?: emptyList(),
            )
        _shareFolder.value = shareFolder
    }

    companion object {
        private const val NOT_INITIALIZED: Long = 0L
        private const val DEFAULT_FOLDER_NAME = "기본 폴더"

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
