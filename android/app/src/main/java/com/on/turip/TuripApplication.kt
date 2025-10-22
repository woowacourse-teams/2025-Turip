package com.on.turip

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.on.turip.common.TuripDebugTree
import com.on.turip.common.TuripReleaseTree
import com.on.turip.data.initializer.FirebaseInstallationsInitializer
import com.on.turip.di.ApplicationContextProvider
import com.on.turip.di.RepositoryModule
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
class TuripApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        CoroutineScope(Dispatchers.IO).launch {
            ApplicationContextProvider.setupApplicationContext(this@TuripApplication)

            FirebaseInstallationsInitializer(RepositoryModule.userStorageRepository)
                .setupFirebaseInstallationId()

            val fid: String =
                RepositoryModule.userStorageRepository
                    .loadId()
                    .getOrNull()
                    ?.fid
                    ?: "unknown"

            FirebaseCrashlytics.getInstance().setUserId(fid)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(TuripDebugTree())
        } else {
            Timber.plant(TuripReleaseTree())
        }
    }
}
