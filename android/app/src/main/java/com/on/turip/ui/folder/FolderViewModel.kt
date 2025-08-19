package com.on.turip.ui.folder

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
import com.on.turip.ui.common.mapper.toEditUiModel
import com.on.turip.ui.folder.model.FolderEditModel
import com.on.turip.ui.folder.model.FolderNameStatusModel
import kotlinx.coroutines.launch
import timber.log.Timber

class FolderViewModel(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val _folders: MutableLiveData<List<FolderEditModel>> = MutableLiveData()
    val folders: LiveData<List<FolderEditModel>> get() = _folders

    private val _inputFolderName: MutableLiveData<String> = MutableLiveData("")
    val inputFolderName: LiveData<String> get() = _inputFolderName

    val folderNameStatus: LiveData<FolderNameStatusModel> =
        inputFolderName.map { FolderNameStatusModel.of(it, LinkedHashSet()) }

    init {
        loadFolders()
    }

    fun loadFolders() {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("찜 폴더 목록 설정 화면 폴더 불러오기 성공")
                    _folders.value =
                        folders.filter { !it.isDefault }.map { folder -> folder.toEditUiModel() }
                }.onFailure { Timber.e("찜 폴더 목록 설정 화면 폴더 불러오기 API 호출 실패") }
        }
    }

    fun updateInputFolderName(input: String) {
        _inputFolderName.value = input
    }

    fun addFolder() {
        viewModelScope.launch {
            inputFolderName.value?.let { folderName: String ->
                folderRepository
                    .createFavoriteFolder(folderName)
                    .onSuccess {
                        Timber.d("폴더 생성 완료(폴더명 = $folderName")
                        _folders.value =
                            folders.value?.plus(FolderEditModel(name = folderName))
                    }.onFailure { Timber.e("폴더 생성 실패") }
            }
        }
    }

    fun selectFolder(folderId: Long) {
        _folders.value =
            folders.value?.map { if (it.id == folderId) it.copy(isSelected = true) else it }
    }

    fun updateFolderName() {
        viewModelScope.launch {
            val folder: FolderEditModel? = folders.value?.first { it.isSelected }
            if (folder != null) {
                inputFolderName.value?.let { updateFolderName: String ->
                    folderRepository
                        .updateFavoriteFolder(folder.id, updateFolderName)
                        .onSuccess {
                            Timber.d("폴더 수정 완료(폴더명 = $updateFolderName}")
                            _folders.value =
                                folders.value?.map { if (it.id == folder.id) it.copy(name = updateFolderName) else it }
                        }.onFailure { Timber.e("폴더 수정 실패") }
                }
            }
        }
    }

    companion object {
        fun provideFactory(folderRepository: FolderRepository = RepositoryModule.folderRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FolderViewModel(
                        folderRepository,
                    )
                }
            }
    }
}
