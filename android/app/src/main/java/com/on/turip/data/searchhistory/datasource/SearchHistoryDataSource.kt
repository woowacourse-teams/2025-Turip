package com.on.turip.data.searchhistory.datasource

import com.on.turip.data.searchhistory.SearchHistoryEntity

interface SearchHistoryDataSource {
    suspend fun createSearchHistory(keyword: String): Result<Unit>

    suspend fun getRecentSearches(limit: Int): Result<List<SearchHistoryEntity>>

    suspend fun deleteSearch(keyword: String): Result<Unit>
}
