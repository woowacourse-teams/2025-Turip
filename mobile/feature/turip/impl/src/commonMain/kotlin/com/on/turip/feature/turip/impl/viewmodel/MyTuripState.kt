package com.on.turip.feature.turip.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.Turip
import com.on.turip.core.ui.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class MyTuripState(
    val turips: ImmutableList<Turip> = persistentListOf(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val selectedTab: MyTuripTab = MyTuripTab.ALL,
    val showAddBottomSheet: Boolean = false,
    val inputTuripName: String = "",
    val isCreatingTurip: Boolean = false,
    val dialogState: MyTuripDialogState? = null,
) : UiState {
    val filteredTurips: ImmutableList<Turip>
        get() = when (selectedTab) {
            MyTuripTab.ALL -> turips
            MyTuripTab.SOLO -> turips.filter { !it.isShared }.toImmutableList()
            MyTuripTab.TOGETHER -> turips.filter { it.isShared }.toImmutableList()
        }
}

enum class MyTuripTab(val label: String) {
    ALL("전체"),
    SOLO("나홀로"),
    TOGETHER("함께"),
}

sealed interface MyTuripDialogState {
    data class RemoveTurip(val turip: Turip) : MyTuripDialogState
}
