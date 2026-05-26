package com.on.turip.core.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [SearchHistoryEntity::class], version = 1)
@ConstructedBy(TuripDatabaseConstructor::class)
abstract class TuripDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TuripDatabaseConstructor : RoomDatabaseConstructor<TuripDatabase> {
    override fun initialize(): TuripDatabase
}
