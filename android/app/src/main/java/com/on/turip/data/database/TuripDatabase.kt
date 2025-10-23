package com.on.turip.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.on.turip.data.searchhistory.SearchHistoryEntity
import com.on.turip.data.searchhistory.dao.SearchHistoryDao

@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class TuripDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}
