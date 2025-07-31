package com.on.turip

import android.app.Application
import com.on.turip.common.TuripReleaseTree
import timber.log.Timber

class TuripApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(TuripReleaseTree())
        }
    }
}
