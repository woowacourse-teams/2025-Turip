package com.on.turip.core.local.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.on.turip.core.data.datasource.InstallReferrerDataSource
import com.on.turip.core.local.database.TuripDatabase
import com.on.turip.core.local.datasourceimpl.DefaultInstallReferrerDataSource
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

val localModuleIos = module {
    single<TuripDatabase> {
        Room.databaseBuilder<TuripDatabase>(
            name = NSHomeDirectory() + "/turip.db",
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    single { get<TuripDatabase>().searchHistoryDao() }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                "${applicationSupportDirectory()}/datastore/turip_prefs.preferences_pb".toPath()
            },
        )
    }

    single<InstallReferrerDataSource> {
        DefaultInstallReferrerDataSource()
    }
}

private fun applicationSupportDirectory(): String =
    NSSearchPathForDirectoriesInDomains(
        directory = NSApplicationSupportDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true,
    ).firstOrNull() as? String ?: NSHomeDirectory()
