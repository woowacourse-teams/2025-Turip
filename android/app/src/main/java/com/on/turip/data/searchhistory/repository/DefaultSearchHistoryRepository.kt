package com.on.turip.data.searchhistory.repository

import com.on.turip.data.searchhistory.datasource.SearchHistoryDataSource
import com.on.turip.data.searchhistory.toDomain
import com.on.turip.domain.searchhistory.SearchHistory
import com.on.turip.domain.searchhistory.SearchHistoryRepository
import timber.log.Timber
import javax.inject.Inject

class DefaultSearchHistoryRepository @Inject constructor(
    private val searchHistoryDataSource: SearchHistoryDataSource,
) : SearchHistoryRepository {
    override suspend fun createSearchHistory(keyword: String): Result<Unit> =
        runCatching {
            searchHistoryDataSource.createSearchHistory(keyword)
        }.onFailure { exception: Throwable ->
            Timber.e(
                exception,
                "SearchHistoryRepository 검색 기록 추가 실패 (keyword=$keyword)",
            )
        }

    override suspend fun loadRecentSearches(limit: Int): Result<List<SearchHistory>> =
        runCatching {
            searchHistoryDataSource
                .getRecentSearchHistories(limit)
                .map { it.toDomain() }
        }.onFailure { exception: Throwable ->
            Timber.e(
                exception,
                "SearchHistoryRepository 최근 검색어 불러오기 실패",
            )
        }

    override suspend fun deleteSearch(keyword: String): Result<Unit> =
        runCatching {
            searchHistoryDataSource.deleteSearch(keyword)
        }.onFailure { exception: Throwable ->
            Timber.e(
                exception,
                "SearchHistoryRepository 검색어 삭제 실패 (keyword=$keyword)",
            )
        }
}
