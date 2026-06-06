package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.network.datasource.SearchHistoryDataSource

class DefaultSearchHistoryDataSource(
    private val dao: SearchHistoryDao,
) : SearchHistoryDataSource {
    override suspend fun createSearchHistory(keyword: String): Unit = dao.insertSearchHistory(SearchHistoryEntity(keyword = keyword))

    override suspend fun getRecentSearchHistories(limit: Int): List<SearchHistoryEntity> = dao.getRecentSearchHistories(limit)

    override suspend fun deleteSearch(keyword: String): Unit = dao.deleteSearch(keyword)
}
