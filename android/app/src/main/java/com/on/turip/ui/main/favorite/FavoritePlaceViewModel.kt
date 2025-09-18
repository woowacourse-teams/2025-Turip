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
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.domain.trip.Place
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
    private val _folders: MutableLiveData<List<FavoritePlaceFolderModel>> = MutableLiveData()
    val folders: LiveData<List<FavoritePlaceFolderModel>> get() = _folders
    private val _places: MutableLiveData<List<FavoritePlaceModel>> = MutableLiveData()
    val places: LiveData<List<FavoritePlaceModel>> get() = _places

    private val _networkError: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: LiveData<Boolean> get() = _networkError

    private val _serverError: MutableLiveData<Boolean> = MutableLiveData(false)
    val serverError: LiveData<Boolean> get() = _serverError

    private var selectedFolderId: Long = NOT_INITIALIZED

    fun loadFoldersAndPlaces() {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("장소 찜 목록 화면 폴더 불러오기 성공")
                    if (selectedFolderId == NOT_INITIALIZED) selectedFolderId = folders[0].id
                    _folders.value =
                        folders.map { folder: Folder -> folder.toUiModel(selectedFolderId) }
                    loadPlacesInSelectFolder()
                    _serverError.value = false
                    _networkError.value = false
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("장소 찜 목록 화면 폴더 불러오기 API 호출 실패")
                }
        }
    }

    private suspend fun loadPlacesInSelectFolder() {
        favoritePlaceRepository
            .loadFavoritePlaces(selectedFolderId)
            .onSuccess { places: List<Place> ->
                _places.value = places.map { place: Place -> place.toUiModel() }
                _serverError.value = false
                _networkError.value = false
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
                    Timber.d("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 성공")
                    if (updatedFavorite) {
                        _places.value =
                            places.value?.map {
                                if (it.placeId == placeId) it.copy(isFavorite = true) else it
                            }
                    } else {
                        _places.value =
                            _places.value
                                ?.filter { it.placeId != placeId }
                    }

                    _serverError.value = false
                    _networkError.value = false
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("찜 목록 화면 폴더명에 해당하는 찜 장소들 업데이트 실패 (placeId =$placeId)")
                }
        }
    }

    fun updateFolderWithPlaces(folderId: Long) {
        val isDeleted: Boolean = folders.value?.all { it.id != folderId } ?: true
        selectedFolderId = if (isDeleted) folders.value?.get(0)?.id ?: NOT_INITIALIZED else folderId

        _folders.value =
            folders.value?.map { favoriteFolder: FavoritePlaceFolderModel ->
                favoriteFolder.copy(isSelected = favoriteFolder.id == selectedFolderId)
            }
        viewModelScope.launch {
            favoritePlaceRepository
                .loadFavoritePlaces(folderId)
                .onSuccess { result: List<Place> ->
                    _places.value = result.map { it.toUiModel() }
                    _serverError.value = false
                    _networkError.value = false
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("폴더에 담긴 장소들을 불러오는 API 호출 실패")
                }
        }
    }

    private fun checkError(errorEvent: ErrorEvent) {
        when (errorEvent) {
            ErrorEvent.USER_NOT_HAVE_PERMISSION -> {
                _serverError.value = true
            }

            ErrorEvent.DUPLICATION_FOLDER -> throw IllegalArgumentException("발생할 수 없는 오류")
            ErrorEvent.UNEXPECTED_PROBLEM -> {
                _serverError.value = true
            }

            ErrorEvent.NETWORK_ERROR -> {
                _networkError.value = true
            }

            ErrorEvent.PARSER_ERROR -> {
                _serverError.value = true
            }
        }
    }

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
