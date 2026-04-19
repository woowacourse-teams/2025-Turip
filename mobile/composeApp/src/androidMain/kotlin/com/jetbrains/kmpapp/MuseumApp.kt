package com.on.turip

import android.app.Application
import com.on.turip.di.initKoin

class MuseumApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
