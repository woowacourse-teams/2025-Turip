package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoritePlaceViewModel(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val _folders: MutableLiveData<List<FavoritePlaceFolderModel>> = MutableLiveData()
    val folders: LiveData<List<FavoritePlaceFolderModel>> get() = _folders
    val placeCount: LiveData<Int> =
        folders.map { favoritePlaceFolders: List<FavoritePlaceFolderModel> ->
            favoritePlaceFolders.firstOrNull { it.isSelected }?.placeCount ?: 0
        }

    private var selectedFolderId: Long = NOT_INITIALIZED

    fun loadFoldersAndPlaces() {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("장소 찜 목록 화면 폴더 불러오기 성공")
                    if (selectedFolderId == NOT_INITIALIZED) selectedFolderId = folders[0].id
                    _folders.value = folders.map { folder -> folder.toUiModel(selectedFolderId) }
                }.onFailure { Timber.e("장소 찜 목록 화면 폴더 불러오기 API 호출 실패") }
        }
    }

    fun updateFolderWithPlaces(folderId: Long) {
        val isDeleted: Boolean = folders.value?.all { it.id != folderId } ?: true
        selectedFolderId = if (isDeleted) folders.value?.get(0)!!.id else folderId

        _folders.value =
            folders.value?.map { favoriteFolder: FavoritePlaceFolderModel ->
                favoriteFolder.copy(isSelected = favoriteFolder.id == selectedFolderId)
            }
        // TODO : 폴더에 맞는 장소 데이터로 업데이트 하기
    }

    companion object {
        private const val NOT_INITIALIZED: Long = 0L

        fun provideFactory(folderRepository: FolderRepository = RepositoryModule.folderRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FavoritePlaceViewModel(
                        folderRepository,
                    )
                }
            }
    }
}
