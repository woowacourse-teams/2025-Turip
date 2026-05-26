package com.on.turip

import android.app.Application
import com.jetbrains.kmpapp.di.initLogger
import com.jetbrains.kmpapp.di.initializeSentry
import com.on.turip.di.initKoin
import org.koin.android.ext.koin.androidContext

class TuripApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initializeSentry()
        initLogger()
        initKoin {
            androidContext(this@TuripApplication)
        }
    }
}
