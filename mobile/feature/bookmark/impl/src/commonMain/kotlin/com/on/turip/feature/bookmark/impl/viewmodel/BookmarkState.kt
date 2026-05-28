package com.on.turip.feature.bookmark.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.Content
import com.on.turip.core.ui.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class BookmarkState(
    val contents: ImmutableList<Content> = persistentListOf(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val isError: Boolean = false,
) : UiState {
    val isEmpty: Boolean get() = !isLoading && !isError && contents.isEmpty()
}
