package com.on.turip.core.common.deeplink

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Android warm-start용 passthrough [DeepLinkEventSource].
 *
 * - 기존 warm-start 전달 특성을 유지한다(at-least-once 보장 대상 아님, pending 저장/재전달 없음).
 * - [acknowledge]는 보관 상태가 없으므로 항상 true.
 * - 중복 네비게이션 방지는 소비 측 [InvitationNavigationCoordinator]가 담당한다.
 */
@OptIn(ExperimentalUuidApi::class)
class PassthroughDeepLinkEventSource(
    urls: Flow<String>,
    parse: (String?) -> DeepLinkParseResult = ::parseDeepLinkSafely,
    newEventId: () -> String = { Uuid.random().toString() },
) : DeepLinkEventSource {
    override val events: Flow<DeepLinkEvent> =
        urls.mapNotNull { url ->
            (parse(url) as? DeepLinkParseResult.Supported)
                ?.let { DeepLinkEvent(id = newEventId(), route = it.route, originalUrl = url) }
        }

    override fun acknowledge(event: DeepLinkEvent): Boolean = true
}
