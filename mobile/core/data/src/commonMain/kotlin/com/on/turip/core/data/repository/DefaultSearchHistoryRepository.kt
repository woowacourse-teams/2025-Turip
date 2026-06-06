package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.SearchHistoryDataSource
import com.on.turip.core.domain.repository.SearchHistoryRepository
import com.on.turip.core.model.searchhistory.SearchHistory

class DefaultSearchHistoryRepository(
    private val searchHistoryDataSource: SearchHistoryDataSource,
) : SearchHistoryRepository {
    override suspend fun createSearchHistory(keyword: String): Result<Unit> =
        runCatching {
            searchHistoryDataSource.createSearchHistory(keyword)
        }

    override suspend fun loadRecentSearches(limit: Int): Result<List<SearchHistory>> =
        runCatching {
            searchHistoryDataSource.getRecentSearchHistories(limit)
        }

    override suspend fun deleteSearch(keyword: String): Result<Unit> =
        runCatching {
            searchHistoryDataSource.deleteSearch(keyword)
        }
}