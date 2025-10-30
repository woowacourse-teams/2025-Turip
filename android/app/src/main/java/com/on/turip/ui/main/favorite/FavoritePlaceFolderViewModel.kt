package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.main.favorite.FavoritePlaceFolderFragment.Companion.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoritePlaceFolderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val folderRepository: FolderRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    private val _favoritePlaceFolders: MutableLiveData<List<FavoritePlaceFolderModel>> =
        MutableLiveData(emptyList())
    val favoritePlaceFolders: LiveData<List<FavoritePlaceFolderModel>> get() = _favoritePlaceFolders

    val hasFavoriteFolderWithPlaceId: LiveData<Boolean> =
        favoritePlaceFolders.map { folders: List<FavoritePlaceFolderModel> ->
            folders.any { folder: FavoritePlaceFolderModel -> folder.isSelected }
        }

    private val placeId: Long by lazy {
        checkNotNull(savedStateHandle[FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID]) {
            Timber.e("폴더 목록 화면 place ID 값이 존재하지 않습니다.")
        }
    }

    fun loadFavoriteFoldersByPlaceId(placeId: Long = this.placeId) {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFoldersStatusByPlaceId(placeId)
                .onSuccess { favoriteFolders: List<FavoriteFolder> ->
                    _favoritePlaceFolders.value = favoriteFolders.map { it.toUiModel() }
                    Timber.d("상세 페이지에서 장소에 대한 찜 폴더 현황 데이터 불러오기 성공 ")
                }.onFailure {
                    Timber.e("상세 페이지에서 장소에 대한 찜 폴더 현황 데이터 불러오기 실패")
                }
        }
    }

    fun updateFolder(
        folderId: Long,
        isFavorite: Boolean,
    ) {
        viewModelScope.launch {
            val updateFavoritesStatus: Boolean = !isFavorite
            updateFavoritePlaceUseCase(folderId, placeId, updateFavoritesStatus)
                .onSuccess {
                    Timber.d("장소에 대한 찜 폴더들 현황에서 장소 찜 업데이트")
                    _favoritePlaceFolders.value =
                        _favoritePlaceFolders.value?.map { folder: FavoritePlaceFolderModel ->
                            if (folder.id == folderId) folder.copy(isSelected = !folder.isSelected) else folder
                        }
                }.onFailure {
                    Timber.e("장소에 대한 찜 폴더들 현황에서 장소 찜 실패")
                }
        }
    }
}
