package com.on.turip.ui.compose.turip

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.designsystem.model.TuripNameStatusModel
import com.on.turip.ui.compose.turip.component.MyTuripModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MyTuripUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val turips: ImmutableList<MyTuripModel>,
    val showAddBottomSheet: Boolean,
    val turipNameStatus: TuripNameStatusModel,
    val inputTuripName: String,
    val deletedTuripId: Long,
    val showTuripRemoveDialog: Boolean,
) {
    companion object {
        val Idle =
            MyTuripUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                turips = persistentListOf(),
                showAddBottomSheet = false,
                turipNameStatus = TuripNameStatusModel.EMPTY,
                inputTuripName = "",
                deletedTuripId = -1L,
                showTuripRemoveDialog = false,
            )
    }
}
