package com.on.turip.domain.session

/**
 * 앱의 현재 인증 세션 상태를 나타내는 모델
 */
sealed interface SessionState {
    /** 검증 전 초기 상태 */
    data object Uninitialized : SessionState

    /** 로그인되지 않은 게스트 상태 */
    data object Guest : SessionState

    /** 인증이 완료된 회원 상태 */
    data object Member : SessionState
}
