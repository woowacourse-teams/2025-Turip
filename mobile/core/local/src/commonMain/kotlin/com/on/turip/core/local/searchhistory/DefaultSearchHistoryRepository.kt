package com.on.turip.core.local.searchhistory

import com.on.turip.core.local.database.SearchHistoryDao
import com.on.turip.core.local.database.SearchHistoryEntity
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class DefaultSearchHistoryRepository(
    private val searchHistoryDao: SearchHistoryDao,
) : SearchHistoryRepository {
    override suspend fun createSearchHistory(keyword: String): Result<Unit> =
        runCatching {
            searchHistoryDao.insertSearchHistory(
                SearchHistoryEntity(
                    keyword = keyword,
                    history = TODO(),
                )
            )
        }.recoverCancellation()

    override suspend fun loadRecentSearches(limit: Int): Result<List<SearchHistory>> =
        runCatching {
            searchHistoryDao.getRecentSearchHistories(limit).map {
                SearchHistory(keyword = it.keyword, timestamp = it.history)
            }
        }.recoverCancellation()

    override suspend fun deleteSearch(keyword: String): Result<Unit> =
        runCatching {
            searchHistoryDao.deleteSearch(keyword)
        }.recoverCancellation()
}

private suspend fun <T> Result<T>.recoverCancellation(): Result<T> {
    if (isFailure) {
        currentCoroutineContext().ensureActive()
    }
    return this
}
