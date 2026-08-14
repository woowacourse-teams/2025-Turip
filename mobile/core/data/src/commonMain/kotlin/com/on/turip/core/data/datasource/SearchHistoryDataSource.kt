package com.on.turip.core.data.datasource

import com.on.turip.core.model.searchhistory.SearchHistory

interface SearchHistoryDataSource {
    suspend fun createSearchHistory(keyword: String)

    suspend fun getRecentSearchHistories(limit: Int): List<SearchHistory>

    suspend fun deleteSearch(keyword: String)
}
