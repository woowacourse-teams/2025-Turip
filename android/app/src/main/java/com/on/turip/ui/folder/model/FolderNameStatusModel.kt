package com.on.turip.ui.folder.model

import androidx.annotation.StringRes
import com.on.turip.R
import com.on.turip.domain.folder.FolderNameStatus

enum class FolderNameStatusModel(
    @StringRes val errorMessage: Int?,
    val isConfirmEnabled: Boolean,
) {
    OK(null, true),
    EMPTY(null, false),
    DUPLICATE_NAME(R.string.all_folder_name_error_duplicate, false),
    DEFAULT_FOLDER_NAME(R.string.all_folder_name_error_default_folder, false),
    MAX_LENGTH_FOLDER_NAME(R.string.all_folder_name_warning_max_length, true),
    OUT_OF_BOUND_LENGTH(R.string.all_folder_name_error_out_of_bound, false),
    ;

    companion object {
        fun of(
            folderName: String,
            originFolders: List<FolderEditModel>,
        ): FolderNameStatusModel {
            val originFolderNames: LinkedHashSet<String> =
                originFolders.map { it.name }.toCollection(LinkedHashSet())

            val folderNameStatus: FolderNameStatus =
                FolderNameStatus.of(folderName, originFolderNames)
            return when (folderNameStatus) {
                FolderNameStatus.EMPTY -> EMPTY
                FolderNameStatus.MAX_LENGTH_FOLDER_NAME -> MAX_LENGTH_FOLDER_NAME
                FolderNameStatus.OUT_OF_BOUND_LENGTH -> OUT_OF_BOUND_LENGTH
                FolderNameStatus.DEFAULT_FOLDER_NAME -> DEFAULT_FOLDER_NAME
                FolderNameStatus.DUPLICATE_NAME -> DUPLICATE_NAME
                FolderNameStatus.OK -> OK
            }
        }
    }
}
