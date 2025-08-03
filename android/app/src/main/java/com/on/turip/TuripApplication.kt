package com.on.turip

import android.app.Application
import com.on.turip.di.DataSourceModule
import com.on.turip.di.RepositoryModule
import com.on.turip.initializer.FirebaseInstallationsInitializer

class TuripApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        DataSourceModule.setupSettingsStorageLocalDataSource(applicationContext)
        FirebaseInstallationsInitializer(RepositoryModule.settingsStorageRepository).setupFirebaseInstallationId()
    }
}
