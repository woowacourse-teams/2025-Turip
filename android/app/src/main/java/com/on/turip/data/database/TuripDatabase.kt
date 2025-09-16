package com.on.turip.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.on.turip.data.searchhistory.SearchHistoryEntity
import com.on.turip.data.searchhistory.dao.SearchHistoryDao

@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class TuripDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        private const val TURIP_DATABASE = "turip_database"

        @Volatile
        private var instance: TuripDatabase? = null

        fun getDataBase(context: Context): TuripDatabase =
            instance ?: synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            TuripDatabase::class.java,
                            TURIP_DATABASE,
                        ).build()
                this.instance = instance

                instance
            }
    }
}
