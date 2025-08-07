package com.on.turip.data.searchhistory.datasource

import com.on.turip.data.dao.SearchHistoryDao
import com.on.turip.data.searchhistory.SearchHistoryEntity

class DefaultSearchHistoryDataSource(
    private val dao: SearchHistoryDao,
) : SearchHistoryDataSource {
    override suspend fun createSearchHistory(keyword: String): Result<Unit> =
        runCatching { dao.insertSearchHistory(SearchHistoryEntity(keyword = keyword)) }

    override suspend fun getRecentSearches(limit: Int): Result<List<SearchHistoryEntity>> = runCatching { dao.getRecentSearches(limit) }

    override suspend fun deleteSearch(keyword: String): Result<Unit> =
        runCatching {
            dao.deleteSearch(
                keyword,
            )
        }

    override suspend fun deleteOldSearches(limit: Int): Result<Unit> =
        runCatching {
            dao.deleteOldSearches(limit)
        }
}
