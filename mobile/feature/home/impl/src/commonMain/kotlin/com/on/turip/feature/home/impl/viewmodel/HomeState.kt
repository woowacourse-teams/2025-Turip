package com.on.turip.feature.home.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.RegionCategory
import com.on.turip.core.model.UsersLikeContent
import com.on.turip.core.ui.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HomeState(
    val isLoading: Boolean = true,
    val regionCategories: ImmutableList<RegionCategory> = persistentListOf(),
    val isDomesticSelected: Boolean = true,
    val usersLikeContents: ImmutableList<UsersLikeContent> = persistentListOf(),
    val isError: Boolean = false,
) : UiState
