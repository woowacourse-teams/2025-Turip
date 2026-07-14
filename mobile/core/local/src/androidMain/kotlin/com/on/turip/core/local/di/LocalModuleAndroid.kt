package com.on.turip.core.local.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.on.turip.core.data.datasource.InstallReferrerDataSource
import com.on.turip.core.local.database.TuripDatabase
import com.on.turip.core.local.datasourceimpl.DefaultInstallReferrerDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModuleAndroid = module {
    single<TuripDatabase> {
        val context = androidContext()
        Room
            .databaseBuilder<TuripDatabase>(
                context = context,
                name = context.getDatabasePath("turip.db").absolutePath,
            ).setDriver(BundledSQLiteDriver())
            .build()
    }

    single { get<TuripDatabase>().searchHistoryDao() }

    single<DataStore<Preferences>> {
        val context = androidContext()
        PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile("turip_prefs")
            },
        )
    }

    single<InstallReferrerDataSource> {
        DefaultInstallReferrerDataSource(androidContext())
    }
}
