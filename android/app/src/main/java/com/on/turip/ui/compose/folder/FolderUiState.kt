package com.on.turip.ui.compose.folder

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.folder.component.MyTuripModel
import com.on.turip.ui.folder.model.TuripNameStatusModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class FolderUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val turips: ImmutableList<MyTuripModel>,
    val showAddBottomSheet: Boolean,
    val turipNameStatus: TuripNameStatusModel,
    val inputTuripName: String,
    val deletedTuripId: Long,
) {
    companion object {
        val Idle =
            FolderUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                turips = persistentListOf(),
                showAddBottomSheet = false,
                turipNameStatus = TuripNameStatusModel.EMPTY,
                inputTuripName = "",
                deletedTuripId = -1L,
            )
    }
}
