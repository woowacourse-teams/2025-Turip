package com.on.turip.core.domain.session

import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val state: StateFlow<SessionState>

    suspend fun switchToGuest()

    fun switchToMember()
}
