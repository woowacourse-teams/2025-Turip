package com.on.turip

import com.google.firebase.messaging.FirebaseMessagingService
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.fcm.FcmTokenRegistrar
import com.on.turip.core.domain.session.SessionState
import org.koin.android.ext.android.inject

class TuripFirebaseMessagingService : FirebaseMessagingService() {
    private val sessionManager: SessionManager by inject()
    private val fcmTokenRegistrar: FcmTokenRegistrar by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (sessionManager.state.value != SessionState.Member) return

        fcmTokenRegistrar.register()
    }
}
