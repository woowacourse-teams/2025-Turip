package com.on.turip.data.session

import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSessionStore @Inject constructor() : SessionStore {
    private val _state = MutableStateFlow<SessionState>(SessionState.Uninitialized)
    override val state: StateFlow<SessionState> = _state.asStateFlow()

    override fun setMember() {
        _state.value = SessionState.Member
    }

    override fun setGuest() {
        _state.value = SessionState.Guest
    }
}
