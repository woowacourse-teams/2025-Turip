package com.on.turip.feature.bookmark.impl.paging

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.error.ErrorUiState
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class PagingState<T>(
    val items: ImmutableList<T>,
    val hasNext: Boolean,
    val isAppending: Boolean,
    val errorUiState: ErrorUiState,
)
