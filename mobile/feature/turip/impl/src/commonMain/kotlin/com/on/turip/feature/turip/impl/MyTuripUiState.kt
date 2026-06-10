package com.on.turip.feature.turip.impl

import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.feature.turip.impl.model.MyTuripModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MyTuripUiState(
    val isLoading: Boolean,
    val isCreatingTurip: Boolean,
    val errorUiState: ErrorUiState,
    val turips: ImmutableList<MyTuripModel>,
    val showAddBottomSheet: Boolean,
    val turipNameStatus: TuripNameStatusModel,
    val inputTuripName: String,
    val dialogState: MyTuripDialogState?,
) {
    companion object {
        val Idle =
            MyTuripUiState(
                isLoading = false,
                isCreatingTurip = false,
                errorUiState = ErrorUiState.None,
                turips = persistentListOf(),
                showAddBottomSheet = false,
                turipNameStatus = TuripNameStatusModel.EMPTY,
                inputTuripName = "",
                dialogState = null,
            )
    }

    sealed interface MyTuripDialogState {
        data class RemoveTurip(
            val turip: MyTuripModel,
        ) : MyTuripDialogState
    }
}
