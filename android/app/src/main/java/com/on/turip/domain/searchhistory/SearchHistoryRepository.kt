package com.on.turip.domain.searchhistory

interface SearchHistoryRepository {
    suspend fun createSearchHistory(keyword: String): Result<Unit>

    suspend fun loadRecentSearches(limit: Int): Result<List<SearchHistory>>

    suspend fun deleteSearch(keyword: String): Result<Unit>
}
