package com.on.turip

import android.app.Application
import android.util.Log
import com.google.firebase.installations.FirebaseInstallations

class TuripApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseInstallations.getInstance().id.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("CN_Log", "Installation ID : ${it.result} ")
            } else {
                Log.d("CN_Log", "Unable to get Installation ID")
            }
        }
    }
}
