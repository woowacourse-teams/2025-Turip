package com.on.turip.core.network.di

import com.on.turip.core.domain.session.AuthTokenCacheController

class DefaultAuthTokenCacheController : AuthTokenCacheController {
    // 등록은 DI 초기화(클라이언트 생성) 시점에 단발성으로 이뤄지고, clear()는 그 이후 호출되므로
    // 복사-후-교체 방식으로 단순하게 스레드 가시성만 보장한다.
    @kotlin.concurrent.Volatile
    private var clearCallbacks: List<() -> Unit> = emptyList()

    internal fun registerOnClear(callback: () -> Unit) {
        clearCallbacks = clearCallbacks + callback
    }

    override fun clear() {
        clearCallbacks.forEach { it.invoke() }
    }
}
