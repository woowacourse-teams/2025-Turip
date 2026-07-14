package com.on.turip.core.common.deeplink

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * 미소비(pending) 딥링크를 보관하는 프로세스 수명 store.
 *
 * 보장하는 의미(정확히 한 번 전달 아님):
 * - 전달: acknowledge 전까지 **at-least-once** (구독자가 없을 때 emit돼도 보관되어 늦은 구독자에게 전달)
 * - 대기 이벤트: **latest-wins** (StateFlow conflation)
 * - 동시 소비: 활성 Consumer 1개 ([collectorMutex] 직렬화)
 * - 최종 네비게이션 1회: Collector 직렬화 + 소비 측 navigator idempotency 조합
 */
@OptIn(ExperimentalUuidApi::class)
class DeepLinkStore(
    private val parse: (String?) -> DeepLinkParseResult = ::parseDeepLinkSafely,
    private val newEventId: () -> String = { Uuid.random().toString() },
) : DeepLinkEventSource {
    private val pendingDeepLink = MutableStateFlow<DeepLinkEvent?>(null)
    private val collectorMutex = Mutex()

    /**
     * 활성 Collector를 직렬화한다. 두 번째 Collector는 첫 번째가 종료(취소)될 때까지 대기하므로
     * 동시에 둘이 붙어도 한 번에 하나만 소비한다.
     */
    override val events: Flow<DeepLinkEvent> =
        flow {
            collectorMutex.lock()
            try {
                emitAll(pendingDeepLink.filterNotNull())
            } finally {
                collectorMutex.unlock()
            }
        }

    fun offer(url: String?) {
        when (val result = parse(url)) {
            is DeepLinkParseResult.Supported -> {
                // id 생성은 update 블록 밖에서 (update 람다는 동시 갱신 시 여러 번 실행될 수 있음).
                val candidate =
                    DeepLinkEvent(
                        id = newEventId(),
                        route = result.route,
                        originalUrl = url.orEmpty(),
                    )
                pendingDeepLink.update { current ->
                    // 같은 링크가 아직 미소비 상태면 중복 이벤트를 만들지 않고 병합한다.
                    if (current?.dedupeKey == candidate.dedupeKey) current else candidate
                }
            }

            // Unsupported는 pending에 저장하지 않는다.
            // 저장하면 latest-wins로 유효한 Invitation pending을 덮어써(= 유실의 다른 형태) 버린다.
            is DeepLinkParseResult.Unsupported -> {
                Napier.w(tag = TAG, message = "지원하지 않는 딥링크입니다. reason=${result.reason}")
            }
        }
    }

    /**
     * 처리한 이벤트가 현재 pending과 동일할 때만 원자적으로 제거한다.
     * A 처리 중/후에 B(또는 동일 dedupeKey의 A2)가 들어왔다면 오래된 ack가 새 값을 지우지 않는다.
     */
    override fun acknowledge(event: DeepLinkEvent): Boolean = pendingDeepLink.compareAndSet(expect = event, update = null)

    companion object {
        private const val TAG = "DeepLinkStore"
    }
}
