package com.on.turip.core.network.datasource

interface SearchHistoryDataSource {
    suspend fun createSearchHistory(keyword: String)

    suspend fun getRecentSearchHistories(limit: Int): List<SearchHistoryEntity>

    suspend fun deleteSearch(keyword: String)
}
