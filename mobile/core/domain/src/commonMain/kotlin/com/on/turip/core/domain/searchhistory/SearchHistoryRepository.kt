package com.on.turip.core.domain.searchhistory

import com.on.turip.core.model.SearchHistory

interface SearchHistoryRepository {
    suspend fun createSearchHistory(keyword: String): Result<Unit>

    suspend fun loadRecentSearches(limit: Int): Result<List<SearchHistory>>

    suspend fun deleteSearch(keyword: String): Result<Unit>
}
