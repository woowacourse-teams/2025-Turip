package com.on.turip.feature.turipdetail.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.Member
import com.on.turip.core.model.Turip
import com.on.turip.core.model.TuripPlace
import com.on.turip.core.ui.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TuripDetailState(
    val turip: Turip? = null,
    val places: ImmutableList<TuripPlace> = persistentListOf(),
    val members: ImmutableList<Member> = persistentListOf(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val showMemberSheet: Boolean = false,
    val showMoreOptionsSheet: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showRenameSheet: Boolean = false,
    val inputName: String = "",
    val isRenaming: Boolean = false,
) : UiState
