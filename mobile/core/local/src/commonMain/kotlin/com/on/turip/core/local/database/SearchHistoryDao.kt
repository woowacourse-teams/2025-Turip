package com.on.turip.core.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(entity: SearchHistoryEntity)

    @Query("SELECT * FROM search_history ORDER BY history DESC LIMIT :limit")
    suspend fun getRecentSearchHistories(limit: Int): List<SearchHistoryEntity>

    @Query("DELETE FROM search_history WHERE keyword = :keyword")
    suspend fun deleteSearch(keyword: String)
}
