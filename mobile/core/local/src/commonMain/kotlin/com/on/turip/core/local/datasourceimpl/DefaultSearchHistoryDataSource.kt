package com.on.turip.core.local.datasourceimpl

import com.on.turip.core.data.datasource.SearchHistoryDataSource
import com.on.turip.core.local.database.SearchHistoryDao
import com.on.turip.core.local.database.SearchHistoryEntity
import com.on.turip.core.model.searchhistory.SearchHistory

class DefaultSearchHistoryDataSource(
    private val dao: SearchHistoryDao,
) : SearchHistoryDataSource {
    override suspend fun createSearchHistory(keyword: String): Unit = dao.insertSearchHistory(SearchHistoryEntity(keyword = keyword))

    override suspend fun getRecentSearchHistories(limit: Int): List<SearchHistory> =
        dao.getRecentSearchHistories(limit).map { entity ->
            SearchHistory(
                keyword = entity.keyword,
                historyTime = entity.history,
            )
        }

    override suspend fun deleteSearch(keyword: String): Unit = dao.deleteSearch(keyword)
}
