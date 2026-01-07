package com.on.turip.data.searchhistory.datasource

import com.on.turip.data.searchhistory.SearchHistoryEntity

interface SearchHistoryDataSource {
    suspend fun createSearchHistory(keyword: String)

    suspend fun getRecentSearchHistories(limit: Int): List<SearchHistoryEntity>

    suspend fun deleteSearch(keyword: String)
}
