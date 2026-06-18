package com.on.turip.core.data.session

import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.domain.session.TokenManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(
    private val tokenManager: TokenManager,
    private val turipRepository: TuripRepository,
    private val bookmarkRepository: BookmarkRepository,
) {
    private val _state = MutableStateFlow<SessionState>(SessionState.Uninitialized)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    /**
     * Guest로 전환
     */
    suspend fun switchToGuest() {
        turipRepository.clearCache()
        bookmarkRepository.clearCache()
        val clearResult: Result<Unit> = tokenManager.clearTokens()
        _state.value = SessionState.Guest

        clearResult.onFailure {
            Napier.e("게스트 전환 중 로컬 토큰 삭제 실패")
        }
    }

    /**
     * Member로 전환
     */
    fun switchToMember() {
        turipRepository.clearCache()
        bookmarkRepository.clearCache()
        _state.value = SessionState.Member
    }
}
