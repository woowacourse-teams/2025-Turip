package com.on.turip.feature.search.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.Content
import com.on.turip.core.model.SearchHistory
import com.on.turip.core.ui.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SearchState(
    val keyword: String = "",
    val searchHistory: ImmutableList<SearchHistory> = persistentListOf(),
    val isHistoryVisible: Boolean = false,
    val resultState: SearchResultState = SearchResultState.Idle,
) : UiState

@Immutable
sealed interface SearchResultState {
    data object Idle : SearchResultState
    data object Loading : SearchResultState
    data class Empty(val keyword: String) : SearchResultState
    data class Success(val contents: ImmutableList<Content>, val totalCount: Int) : SearchResultState
    data object Error : SearchResultState
}
