package com.on.turip.feature.search.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.Content
import com.on.turip.core.ui.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class RegionResultState(
    val regionName: String = "",
    val contents: ImmutableList<Content> = persistentListOf(),
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
    val isError: Boolean = false,
) : UiState
