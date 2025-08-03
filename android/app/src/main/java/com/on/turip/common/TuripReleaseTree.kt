package com.on.turip.common

import android.util.Log
import timber.log.Timber

class TuripReleaseTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        if (priority >= Log.ERROR) {
            // TODO: Firebase crashlytics 연동
        }
    }
}
