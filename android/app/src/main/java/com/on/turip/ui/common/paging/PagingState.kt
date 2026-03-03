package com.on.turip.ui.common.paging

import androidx.compose.runtime.Immutable
import com.on.turip.ui.common.error.ErrorUiState
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class PagingState<T>(
    val items: ImmutableList<T>,
    val hasNext: Boolean,
    val isAppending: Boolean,
    val errorUiState: ErrorUiState,
)
