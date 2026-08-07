package com.on.turip.core.common.deeplink

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeepLinkStoreTest {
    private fun url(token: String) = "https://invite.turip.kro.kr/invitations/?token=$token"

    @Test
    fun `구독 전에 offer된 콜드 스타트 이벤트도 이후 구독자에게 전달된다`() =
        runTest {
            val store = DeepLinkStore(newEventId = incrementingId())

            // 구독자가 붙기 전에 emit (콜드 스타트 상황)
            store.offer(url("A"))

            val event = store.events.first()

            assertEquals(ActionableDeepLinkRoute.Invitation("A").dedupeKey, event.dedupeKey)
        }

    @Test
    fun `이벤트는 원본 URL을 보존한다 - 토큰 재조립 이중 디코딩 방지`() =
        runTest {
            val store = DeepLinkStore(newEventId = incrementingId())
            // 인코딩 문자가 포함된 토큰을 가진 원본 URL
            val original = "https://invite.turip.kro.kr/invitations/?token=aGVsbG8%2Bd29ybGQ%3D"
            store.offer(original)

            assertEquals(original, store.events.first().originalUrl)
        }

    @Test
    fun `A 처리 중 B가 들어오면 ack A는 B를 삭제하지 않는다`() =
        runTest {
            val store = DeepLinkStore(newEventId = incrementingId())
            store.offer(url("A"))
            val eventA = store.events.first()

            // 처리 도중 B 도착
            store.offer(url("B"))

            assertFalse(store.acknowledge(eventA), "오래된 ack(A)는 false여야 한다")
            val eventB = store.events.first()
            assertEquals(ActionableDeepLinkRoute.Invitation("B").dedupeKey, eventB.dedupeKey)
        }

    @Test
    fun `A ack 후 동일 dedupeKey의 A2가 와도 오래된 ack A가 A2를 제거하지 않는다`() =
        runTest {
            val store = DeepLinkStore(newEventId = incrementingId())
            store.offer(url("A"))
            val eventA = store.events.first()
            assertTrue(store.acknowledge(eventA))

            // 동일 링크 재수신 → 새 id의 A2 생성
            store.offer(url("A"))
            val eventA2 = store.events.first()

            // 오래된 eventA로 ack 시도 → CAS 실패 (id가 다름)
            assertFalse(store.acknowledge(eventA))
            assertEquals(eventA2.dedupeKey, store.events.first().dedupeKey)
        }

    @Test
    fun `같은 dedupeKey 연속 offer는 pending 이벤트 하나로 병합된다`() =
        runTest {
            val ids = incrementingId()
            val store = DeepLinkStore(newEventId = ids)
            store.offer(url("A"))
            val firstId = store.events.first().id
            store.offer(url("A")) // 미소비 상태에서 동일 링크 재offer

            assertEquals(firstId, store.events.first().id, "병합되어 같은 이벤트가 유지돼야 한다")
        }

    @Test
    fun `Unsupported offer는 기존 Invitation pending을 덮어쓰지 않는다`() =
        runTest {
            val store = DeepLinkStore(newEventId = incrementingId())
            store.offer(url("A"))
            store.offer("https://invite.turip.kro.kr/invitations/") // token 없음 → Unsupported

            assertEquals(
                ActionableDeepLinkRoute.Invitation("A").dedupeKey,
                store.events.first().dedupeKey,
            )
        }

    @Test
    fun `latest-wins - A 미소비 중 B C 순서로 offer되면 최신 C가 남는다`() =
        runTest {
            val store = DeepLinkStore(newEventId = incrementingId())
            store.offer(url("A"))
            store.offer(url("B"))
            store.offer(url("C"))

            assertEquals(
                ActionableDeepLinkRoute.Invitation("C").dedupeKey,
                store.events.first().dedupeKey,
            )
        }

    @Test
    fun `acknowledge 후에는 pending이 비어 새 구독자에게 전달할 값이 없다`() =
        runTest {
            val store = DeepLinkStore(newEventId = incrementingId())
            store.offer(url("A"))
            val event = store.events.first()
            assertTrue(store.acknowledge(event))

            var received: DeepLinkEvent? = null
            val job =
                launch {
                    store.events.collect { received = it }
                }
            yield()
            assertNull(received, "ack 후 재구독에는 재전달이 없어야 한다")
            job.cancel()
        }

    @Test
    fun `동시에 두 Collector가 붙어도 한 Collector만 이벤트를 소비한다`() =
        runTest {
            val store = DeepLinkStore(newEventId = incrementingId())
            store.offer(url("A"))

            var firstReceived: DeepLinkEvent? = null
            var secondReceived: DeepLinkEvent? = null

            val job1 = launch { store.events.collect { firstReceived = it } }
            yield()
            val job2 = launch { store.events.collect { secondReceived = it } }
            yield()

            assertNotNull(firstReceived, "첫 Collector는 수신해야 한다")
            assertNull(secondReceived, "두 번째 Collector는 직렬화로 대기해 수신하지 못한다")

            job1.cancel()
            job2.cancel()
        }

    private fun incrementingId(): () -> String {
        var counter = 0
        return { "id-${counter++}" }
    }
}
