package com.on.turip.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.on.turip.data.database.TuripDatabase
import com.on.turip.data.searchhistory.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {
    private const val USER_PREFERENCES_FILE_NAME = "user_storage.preferences_pb"
    private const val TURIP_DATABASE = "turip_database"

    @Provides
    @Singleton
    fun provideUserDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.dataStoreFile(USER_PREFERENCES_FILE_NAME)
        }

    @Provides
    @Singleton
    fun provideTuripDatabase(
        @ApplicationContext context: Context,
    ): TuripDatabase =
        Room
            .databaseBuilder(
                context,
                TuripDatabase::class.java,
                TURIP_DATABASE,
            ).build()

    @Provides
    @Singleton
    fun provideTuripDao(turipDatabase: TuripDatabase): SearchHistoryDao = turipDatabase.searchHistoryDao()
}
