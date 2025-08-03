package com.on.turip

import android.app.Application
import com.on.turip.common.TuripDebugTree
import com.on.turip.common.TuripReleaseTree
import com.on.turip.di.DataSourceModule
import com.on.turip.di.RepositoryModule
import com.on.turip.initializer.FirebaseInstallationsInitializer
import timber.log.Timber

class TuripApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(TuripDebugTree())
        } else {
            Timber.plant(TuripReleaseTree())
        }

        DataSourceModule.setupSettingsStorageLocalDataSource(applicationContext)
        FirebaseInstallationsInitializer(RepositoryModule.settingsStorageRepository).setupFirebaseInstallationId()
    }
}
