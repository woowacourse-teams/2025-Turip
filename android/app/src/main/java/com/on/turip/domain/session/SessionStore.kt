package com.on.turip.domain.session

import kotlinx.coroutines.flow.StateFlow

/**
 * 앱 전역에서 인증 세션 상태를 관리하는 저장소
 */
interface SessionStore {
    val state: StateFlow<SessionState>

    fun setMember()

    fun setGuest()
}
