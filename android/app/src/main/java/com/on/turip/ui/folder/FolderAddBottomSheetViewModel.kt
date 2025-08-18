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
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.ui.folder.model.FolderNameStatusModel
import kotlinx.coroutines.launch
import timber.log.Timber

class FolderAddBottomSheetViewModel(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val _inputFolderName: MutableLiveData<String> = MutableLiveData("")
    val inputFolderName: LiveData<String> get() = _inputFolderName

    val folderNameStatus: LiveData<FolderNameStatusModel> =
        inputFolderName.map { FolderNameStatusModel.of(it, LinkedHashSet()) }

    fun createFolder() {
        viewModelScope.launch {
            inputFolderName.value?.let { folderName: String ->
                folderRepository
                    .createFavoriteFolder(folderName)
                    .onSuccess { Timber.d("폴더 생성 완료(폴더명 =$folderName") }
                    .onFailure { Timber.e("폴더 생성 실패") }
            }
        }
    }

    fun updateFolderName(input: String) {
        _inputFolderName.value = input
    }

    companion object {
        fun provideFactory(folderRepository: FolderRepository = RepositoryModule.folderRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FolderAddBottomSheetViewModel(
                        folderRepository,
                    )
                }
            }
    }
}
