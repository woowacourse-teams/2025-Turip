package com.on.turip.data.searchhistory.repository

import com.on.turip.data.searchhistory.SearchHistoryEntity
import com.on.turip.data.searchhistory.datasource.SearchHistoryDataSource
import com.on.turip.data.searchhistory.toDomain
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.domain.searchhistory.SearchHistoryRepository

class DefaultSearchHistoryRepository(
    private val dataSource: SearchHistoryDataSource,
) : SearchHistoryRepository {
    override suspend fun createSearchHistory(keyword: String): Result<Unit> =
        runCatching {
            dataSource.createSearchHistory(keyword)
        }

    override suspend fun loadRecentSearches(limit: Int): Result<List<SearchHistory>> =
        dataSource.getRecentSearches(limit).mapCatching { it: List<SearchHistoryEntity> ->
            it.map { it.toDomain() }
        }

    override suspend fun deleteSearch(keyword: String): Result<Unit> =
        runCatching {
            dataSource.deleteSearch(keyword)
        }
}
