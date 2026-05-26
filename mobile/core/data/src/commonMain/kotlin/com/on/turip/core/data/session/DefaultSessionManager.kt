package com.on.turip.core.data.session

import com.on.turip.core.domain.session.SessionManager
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.model.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultSessionManager(
    private val tokenManager: TokenManager,
) : SessionManager {
    private val _state = MutableStateFlow<SessionState>(SessionState.Unknown)
    override val state: StateFlow<SessionState> = _state.asStateFlow()

    override suspend fun switchToGuest() {
        tokenManager.clearTokens()
        _state.value = SessionState.Guest
    }

    override fun switchToMember() {
        _state.value = SessionState.LoggedIn
    }
}
