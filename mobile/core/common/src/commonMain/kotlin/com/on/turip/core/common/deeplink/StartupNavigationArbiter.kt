package com.on.turip.core.common.deeplink

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private enum class NavigationOwner { None, Session, DeepLink }

/**
 * 앱 시작(startup) 동안 세션 기반 진입과 딥링크 진입의 네비게이션 우선순위를 직렬화한다.
 *
 * - **DeepLink > Session**: 딥링크가 먼저 소유권을 잡으면 이후 세션 네비게이션은 차단된다.
 * - 검사와 네비게이션을 하나의 임계 영역(Mutex)으로 묶어 check-then-act 경쟁을 제거한다.
 * - 앱 싱글턴이 아니라 **1회 startup 수명**으로 한정해야 한다(같은 인스턴스를 Splash 소비자와
 *   딥링크 Collector가 공유). startup 종료 후 일반 세션 네비게이션을 영구 차단하면 안 된다.
 *
 * 계약:
 * - [navigate] 블록은 suspend/별도 coroutine launch/다른 dispatcher post 후 즉시 반환을 하지 말고,
 *   실제 navigation state 변경을 **동기적으로** 수행해야 한다.
 * - 호출은 Main/UI dispatcher에서 한다(Mutex는 실행 순서만 직렬화할 뿐 Main 실행을 보장하지 않음).
 */
class StartupNavigationArbiter {
    private val mutex = Mutex()
    private var owner = NavigationOwner.None

    /**
     * 세션 기반 네비게이션. 아직 아무도 소유권을 잡지 않았을 때만 실행된다.
     * @return 실행되었으면 true, 이미 다른 owner가 선점했으면 false.
     */
    suspend fun navigateBySession(navigate: () -> Unit): Boolean =
        mutex.withLock {
            if (owner != NavigationOwner.None) return@withLock false
            owner = NavigationOwner.Session
            try {
                navigate()
                true
            } catch (throwable: Throwable) {
                owner = NavigationOwner.None
                throw throwable
            }
        }

    /**
     * 딥링크 네비게이션. **결과와 무관하게 호출 즉시 DeepLink 소유권을 선점**하므로,
     * 추가 네비게이션이 없는 경우(idempotent 재진입)에도 이후 세션 네비게이션을 차단한다.
     */
    suspend fun <T> navigateByDeepLink(navigate: () -> T): T =
        mutex.withLock {
            val previousOwner = owner
            owner = NavigationOwner.DeepLink
            try {
                navigate()
            } catch (throwable: Throwable) {
                owner = previousOwner
                throw throwable
            }
        }
}
