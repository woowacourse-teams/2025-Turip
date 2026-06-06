package com.on.turip.core.network.datasource

/**
 * deferred deep link 처리 상태를 로컬에 저장하고 관리한다.
 */
interface DeferredDeepLinkLocalDataSource {
    suspend fun isInstallReferrerHandled(): Boolean

    suspend fun markInstallReferrerHandled()
}
