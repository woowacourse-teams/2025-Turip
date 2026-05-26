package com.on.turip.core.domain.session

import com.on.turip.core.model.SessionState
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val state: StateFlow<SessionState>

    suspend fun switchToGuest()

    fun switchToMember()
}
