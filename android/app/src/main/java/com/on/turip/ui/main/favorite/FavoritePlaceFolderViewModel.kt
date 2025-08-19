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
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoritePlaceFolderViewModel(
    private val placeId: Long,
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val _favoritePlaceFolders: MutableLiveData<List<FavoritePlaceFolderModel>> =
        MutableLiveData(emptyList())
    val favoritePlaceFolders: LiveData<List<FavoritePlaceFolderModel>> get() = _favoritePlaceFolders

    init {
        loadFavoriteFoldersByPlaceId(placeId)
    }

    fun loadFavoriteFoldersByPlaceId(placeId: Long) {
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

    fun updateFolder(folderId: Long) {
        _favoritePlaceFolders.value =
            _favoritePlaceFolders.value?.map { folder: FavoritePlaceFolderModel ->
                if (folder.id == folderId) folder.copy(isSelected = !folder.isSelected) else folder
            }
    }

    companion object {
        fun provideFactory(
            placeId: Long,
            folderRepository: FolderRepository = RepositoryModule.folderRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FavoritePlaceFolderViewModel(
                        placeId,
                        folderRepository,
                    )
                }
            }
    }
}
