package com.on.turip.data.searchhistory.datasource

import com.on.turip.data.searchhistory.SearchHistoryEntity
import com.on.turip.data.searchhistory.dao.SearchHistoryDao
import javax.inject.Inject

class DefaultSearchHistoryDataSource @Inject constructor(
    private val dao: SearchHistoryDao,
) : SearchHistoryDataSource {
    override suspend fun createSearchHistory(keyword: String): Result<Unit> =
        runCatching { dao.insertSearchHistory(SearchHistoryEntity(keyword = keyword)) }

    override suspend fun getRecentSearchHistories(limit: Int): Result<List<SearchHistoryEntity>> =
        runCatching { dao.getRecentSearchHistories(limit) }

    override suspend fun deleteSearch(keyword: String): Result<Unit> =
        runCatching {
            dao.deleteSearch(keyword)
        }
}
