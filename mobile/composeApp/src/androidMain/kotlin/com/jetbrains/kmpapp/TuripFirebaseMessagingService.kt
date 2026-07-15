package com.on.turip

import com.google.firebase.messaging.FirebaseMessagingService
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.domain.usecase.RegisterFcmTokenUseCase
import com.on.turip.core.model.result.onFailureWithCause
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TuripFirebaseMessagingService : FirebaseMessagingService() {
    private val sessionManager: SessionManager by inject()
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (sessionManager.state.value != SessionState.Member) return

        CoroutineScope(Dispatchers.IO).launch {
            registerFcmTokenUseCase().onFailureWithCause { errorType, cause ->
                Napier.w("FCM 토큰 갱신 등록 실패. errorType=$errorType", cause, tag = "FcmToken")
            }
        }
    }
}
