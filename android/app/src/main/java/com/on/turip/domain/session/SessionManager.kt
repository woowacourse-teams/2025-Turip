package com.on.turip.domain.session

import com.on.turip.core.session.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager,
) {
    private val _state = MutableStateFlow<SessionState>(SessionState.Uninitialized)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    /**
     * Guest로 전환
     */
    suspend fun switchToGuest() {
        val clearResult: Result<Unit> = tokenManager.clearTokens()
        _state.value = SessionState.Guest

        clearResult.onFailure {
            Timber.Forest.e("게스트 전환 중 로컬 토큰 삭제 실패")
        }
    }

    /**
     * Member로 전환
     */
    fun switchToMember() {
        _state.value = SessionState.Member
    }
}
