package com.on.turip.core.domain.repository

import com.on.turip.core.model.turip.TuripInvitationToken

interface DeferredDeepLinkRepository {
    /**
     * 성공 시 초대 토큰을 반환한다.
     *
     * 이미 처리되었거나 토큰이 없거나 해석할 수 없는 경우 `null`을 반환한다.
     *
     * 일시적인 시스템 실패는 `Result.failure`로 전달해 상위에서 재시도 정책을 결정할 수 있게 한다.
     *
     */
    suspend fun resolveDeferredInvitationToken(): Result<TuripInvitationToken?>
}
