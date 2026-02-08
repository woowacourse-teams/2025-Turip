package com.on.turip

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.on.turip.common.FidProvider
import com.on.turip.common.TuripDebugTree
import com.on.turip.common.TuripReleaseTree
import com.on.turip.data.initializer.FirebaseInstallationsInitializer
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class TuripApplication : Application() {
    @Inject
    lateinit var userStorageRepository: UserStorageRepository

    @Inject
    lateinit var fidProvider: FidProvider

    override fun onCreate() {
        super.onCreate()
        fidProvider.init()

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        CoroutineScope(Dispatchers.IO).launch {
            FirebaseInstallationsInitializer(userStorageRepository)
                .setupFirebaseInstallationId()

            FirebaseCrashlytics.getInstance().setUserId(fidProvider.cachedFid)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(TuripDebugTree())
        } else {
            Timber.plant(TuripReleaseTree())
        }
    }
}
