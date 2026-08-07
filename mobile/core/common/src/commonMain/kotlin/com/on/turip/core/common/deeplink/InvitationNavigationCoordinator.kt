package com.on.turip.core.common.deeplink

enum class InvitationNavResult {
    /** 실제로 초대 화면으로 네비게이션했다. */
    Navigated,

    /** 같은 초대 흐름을 이미 처리 중이라 추가 네비게이션 없이 통과했다. */
    AlreadyHandling,
}

/**
 * 동일 초대 링크의 중복 네비게이션을 막는 의미 기반 idempotency 가드.
 *
 * - acknowledge 직후 동일 링크가 재수신돼도 `AlreadyHandling`을 반환해 중복 진입을 막는다.
 * - handling 수명: InvitationEntry 진입 시 시작 → 같은 초대 흐름(TuripDetail 포함) 동안 유지 →
 *   그 흐름이 back stack에서 벗어나면 [onFlowEnded]로 해제(이후 동일 링크 재탭 허용).
 */
class InvitationNavigationCoordinator {
    private var activeDedupeKey: String? = null

    fun navigateInvitationIfNeeded(
        dedupeKey: String,
        navigate: () -> Unit,
    ): InvitationNavResult {
        if (activeDedupeKey == dedupeKey) return InvitationNavResult.AlreadyHandling
        activeDedupeKey = dedupeKey
        navigate()
        return InvitationNavResult.Navigated
    }

    /** 초대 흐름이 종료(back stack에서 제거)되었을 때 호출한다. */
    fun onFlowEnded() {
        activeDedupeKey = null
    }
}
