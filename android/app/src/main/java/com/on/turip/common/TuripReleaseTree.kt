package com.on.turip.common

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class TuripReleaseTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        if (priority >= Log.ERROR) {
            FirebaseCrashlytics.getInstance().apply {
                log(message)
                recordException(t ?: Exception(message))
                setCustomKey("log_priority", priority)
            }
        }
    }
}
