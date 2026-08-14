package com.on.turip.core.common.deeplink

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StartupNavigationArbiterTest {
    @Test
    fun `Session이 먼저 선점하면 이후 DeepLink가 덮어쓸 수 있다`() =
        runTest {
            val arbiter = StartupNavigationArbiter()
            val log = mutableListOf<String>()

            assertTrue(arbiter.navigateBySession { log += "session" })
            arbiter.navigateByDeepLink { log += "deeplink" }

            assertEquals(listOf("session", "deeplink"), log)
        }

    @Test
    fun `DeepLink가 먼저 선점하면 이후 Session 네비게이션은 차단된다`() =
        runTest {
            val arbiter = StartupNavigationArbiter()
            val log = mutableListOf<String>()

            arbiter.navigateByDeepLink { log += "deeplink" }
            val sessionRan = arbiter.navigateBySession { log += "session" }

            assertFalse(sessionRan, "DeepLink 선점 후 세션 네비게이션은 실행되면 안 된다")
            assertEquals(listOf("deeplink"), log)
        }

    @Test
    fun `추가 네비게이션이 없어도 navigateByDeepLink는 owner를 선점한다`() =
        runTest {
            val arbiter = StartupNavigationArbiter()

            // AlreadyHandling을 흉내: 실제 네비게이션 없이 결과만 반환
            val result = arbiter.navigateByDeepLink { "AlreadyHandling" }
            assertEquals("AlreadyHandling", result)

            // 이후 세션 네비게이션은 차단돼야 한다
            assertFalse(arbiter.navigateBySession { })
        }

    @Test
    fun `Session 네비게이션 예외 시 owner가 복원되어 재시도가 가능하다`() =
        runTest {
            val arbiter = StartupNavigationArbiter()

            assertFailsWith<IllegalStateException> {
                arbiter.navigateBySession { throw IllegalStateException("boom") }
            }

            // owner가 None으로 복원됐으므로 이후 네비게이션이 가능
            assertTrue(arbiter.navigateBySession { })
        }

    @Test
    fun `DeepLink 네비게이션 예외 시 owner가 이전 상태로 복원된다`() =
        runTest {
            val arbiter = StartupNavigationArbiter()

            assertFailsWith<IllegalStateException> {
                arbiter.navigateByDeepLink<Unit> { throw IllegalStateException("boom") }
            }

            // 이전 owner(None)로 복원됐으므로 세션 네비게이션이 가능
            assertTrue(arbiter.navigateBySession { })
        }
}
