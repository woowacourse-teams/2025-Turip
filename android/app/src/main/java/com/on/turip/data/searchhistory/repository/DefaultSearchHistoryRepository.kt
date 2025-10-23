package com.on.turip.data.searchhistory.repository

import com.on.turip.data.searchhistory.SearchHistoryEntity
import com.on.turip.data.searchhistory.datasource.SearchHistoryDataSource
import com.on.turip.data.searchhistory.toDomain
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.domain.searchhistory.SearchHistoryRepository
import javax.inject.Inject

class DefaultSearchHistoryRepository @Inject constructor(
    private val searchHistoryDataSource: SearchHistoryDataSource,
) : SearchHistoryRepository {
    override suspend fun createSearchHistory(keyword: String): Result<Unit> =
        runCatching {
            searchHistoryDataSource.createSearchHistory(keyword)
        }

    override suspend fun loadRecentSearches(limit: Int): Result<List<SearchHistory>> =
        searchHistoryDataSource
            .getRecentSearchHistories(limit)
            .mapCatching { it: List<SearchHistoryEntity> ->
                it.map { it.toDomain() }
            }

    override suspend fun deleteSearch(keyword: String): Result<Unit> =
        runCatching {
            searchHistoryDataSource.deleteSearch(keyword)
        }
}
