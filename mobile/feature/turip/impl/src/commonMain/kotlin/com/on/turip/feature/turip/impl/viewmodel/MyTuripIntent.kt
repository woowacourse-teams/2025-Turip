package com.on.turip.feature.turip.impl.viewmodel

import com.on.turip.core.model.Turip
import com.on.turip.core.ui.UiIntent

sealed interface MyTuripIntent : UiIntent {
    data object LoadTurips : MyTuripIntent
    data class SelectTab(val tab: MyTuripTab) : MyTuripIntent
    data object ShowAddBottomSheet : MyTuripIntent
    data object DismissAddBottomSheet : MyTuripIntent
    data class UpdateTuripName(val name: String) : MyTuripIntent
    data object ConfirmAddTurip : MyTuripIntent
    data class ShowDeleteDialog(val turip: Turip) : MyTuripIntent
    data object DismissDeleteDialog : MyTuripIntent
    data class ConfirmDelete(val turip: Turip) : MyTuripIntent
}
