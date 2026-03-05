package com.on.turip.data.session

import com.on.turip.domain.session.AuthTokenCacheController
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Ktor Auth Token 캐싱 처리
 */
@Singleton
class DefaultAuthTokenCacheController @Inject constructor(
    private val httpClient: Provider<HttpClient>,
) : AuthTokenCacheController {
    override fun clear() {
        httpClient.get().authProvider<BearerAuthProvider>()?.clearToken()
    }
}
