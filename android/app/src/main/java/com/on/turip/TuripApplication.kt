package com.on.turip

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.on.turip.common.TuripDebugTree
import com.on.turip.common.TuripReleaseTree
import com.on.turip.data.initializer.FirebaseInstallationsInitializer
import com.on.turip.di.ApplicationContextProvider
import com.on.turip.di.RepositoryModule
import timber.log.Timber

class TuripApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
            defaultHandler?.uncaughtException(thread, exception)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(TuripDebugTree())
        } else {
            Timber.plant(TuripReleaseTree())
        }

        ApplicationContextProvider.setupApplicationContext(this)
        FirebaseInstallationsInitializer(RepositoryModule.userStorageRepository).setupFirebaseInstallationId()
    }
}
