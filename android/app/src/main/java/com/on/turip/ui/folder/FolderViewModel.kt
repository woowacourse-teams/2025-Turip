package com.on.turip.ui.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.common.mapper.toEditUiModel
import com.on.turip.ui.folder.model.FolderEditModel
import com.on.turip.ui.folder.model.FolderNameStatusModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val _folders: MutableLiveData<List<FolderEditModel>> = MutableLiveData()
    val folders: LiveData<List<FolderEditModel>> get() = _folders

    private val _inputFolderName: MutableLiveData<String> = MutableLiveData("")
    val inputFolderName: LiveData<String> get() = _inputFolderName

    val folderNameStatus: LiveData<FolderNameStatusModel>
        get() =
            inputFolderName.map { FolderNameStatusModel.of(it, folders.value ?: emptyList()) }

    val selectedFolder: LiveData<FolderEditModel>
        get() =
            folders.map { folders: List<FolderEditModel> ->
                folders.first { folder: FolderEditModel -> folder.isSelected }
            }

    init {
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            folderRepository
                .loadFavoriteFolders()
                .onSuccess { folders: List<Folder> ->
                    Timber.d("찜 폴더 목록 설정 화면 폴더 불러오기 성공")
                    _folders.value =
                        folders
                            .filter { !it.isDefault }
                            .map { folder -> folder.toEditUiModel() }
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
                        Timber.d("폴더 생성 완료(폴더명 = $folderName)")
                        _folders.value =
                            folders.value?.plus(it.toEditUiModel())
                        _inputFolderName.value = ""
                    }.onFailure { Timber.e("폴더 생성 실패") }
            }
        }
    }

    fun selectFolder(folderId: Long) {
        _folders.value =
            folders.value?.map { folderModel: FolderEditModel ->
                folderModel.copy(isSelected = folderModel.id == folderId)
            }
    }

    fun updateFolderName() {
        viewModelScope.launch {
            selectedFolder.value?.let { selectFolder: FolderEditModel ->
                inputFolderName.value?.let { updateFolderName: String ->
                    folderRepository
                        .updateFavoriteFolder(selectFolder.id, updateFolderName)
                        .onSuccess {
                            Timber.d("폴더 수정 완료(폴더명 = $updateFolderName})")
                            _folders.value =
                                folders.value?.map { if (it.id == selectFolder.id) it.copy(name = updateFolderName) else it }
                        }.onFailure { Timber.e("폴더 수정 실패") }
                }
            }
        }
    }

    fun deleteFolder() {
        viewModelScope.launch {
            selectedFolder.value?.let { selectFolder: FolderEditModel ->
                folderRepository
                    .deleteFavoriteFolder(selectFolder.id)
                    .onSuccess {
                        _folders.value = folders.value?.filter { it.id != selectFolder.id }
                        Timber.d("폴더 삭제 완료(폴더명 = ${selectFolder.name})")
                    }.onFailure {
                        Timber.d("폴더 삭제 실패")
                    }
            }
        }
    }
}
