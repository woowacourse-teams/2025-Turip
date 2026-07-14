package com.on.turip.core.common.deeplink

import kotlinx.coroutines.flow.Flow

/**
 * pending 상태로 보관되는 딥링크 이벤트.
 *
 * - [id]: 어떤 pending 이벤트를 소비했는지 식별한다(조건부 acknowledge의 CAS 기준).
 * - [route]: 실행 가능한 라우트만 담는다.
 * - [originalUrl]: 네비게이션에 사용할 **원본 딥링크 URL**. 토큰을 파싱→재조립하면 인코딩이
 *   이중 디코딩되어 토큰이 손상되므로, 원본을 그대로 보존해 화면에 넘긴다.
 * - [dedupeKey]: [route]에서 파생한다(중복 필드를 두지 않아 불일치 불변 조건을 보장).
 *
 * 로그에 토큰이 노출되지 않도록 [toString]은 redacted 처리한다.
 */
data class DeepLinkEvent(
    val id: String,
    val route: ActionableDeepLinkRoute,
    val originalUrl: String,
) {
    val dedupeKey: String get() = route.dedupeKey

    override fun toString(): String = "DeepLinkEvent(id=$id, route=$route, originalUrl=REDACTED)"
}

/**
 * 딥링크 이벤트의 소스. Flow와 acknowledge를 한 인터페이스로 묶어
 * 서로 다른 Flow/acknowledge가 잘못 조합되는 것을 막는다.
 */
interface DeepLinkEventSource {
    val events: Flow<DeepLinkEvent>

    /**
     * 처리 완료를 통지한다. 현재 pending이 [event]와 동일할 때만 제거(true)한다.
     */
    fun acknowledge(event: DeepLinkEvent): Boolean
}
