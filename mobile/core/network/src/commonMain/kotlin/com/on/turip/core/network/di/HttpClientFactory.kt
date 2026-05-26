package com.on.turip.core.network.di

import com.on.turip.core.common.ApiException
import com.on.turip.core.domain.auth.AuthRepository
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.network.error.ErrorResponse
import com.on.turip.core.network.error.toNetworkError
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val CONNECT_TIMEOUT_MS = 10_000L
private const val SOCKET_TIMEOUT_MS = 20_000L
private const val REQUEST_TIMEOUT_MS = 20_000L

private val jsonConfig = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private class TokenCache {
    @kotlin.concurrent.Volatile
    var value: BearerTokens? = null
    fun clear() { value = null }
}

expect fun createDefaultEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient
expect fun createNoAuthEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient
expect fun createSseEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient

fun buildDefaultHttpClient(
    baseUrl: String,
    tokenManager: TokenManager,
    authRepositoryProvider: () -> AuthRepository,
    deviceFidProvider: () -> String?,
    authTokenCacheController: DefaultAuthTokenCacheController,
): HttpClient {
    val tokenCache = TokenCache()
    authTokenCacheController.onClear { tokenCache.clear() }
    return createDefaultEngine {
        configureContentNegotiation()
        configureLogging()
        configureTimeout()
        configureDefaultRequest(baseUrl, deviceFidProvider)
        configureBearerAuth(tokenManager, authRepositoryProvider, tokenCache)
        handleErrors()
    }
}

fun buildNoAuthHttpClient(
    baseUrl: String,
    deviceFidProvider: () -> String?,
): HttpClient = createNoAuthEngine {
    configureContentNegotiation()
    configureLogging()
    configureTimeout()
    configureDefaultRequest(baseUrl, deviceFidProvider)
    handleErrors()
}

fun buildSseHttpClient(
    baseUrl: String,
    tokenManager: TokenManager,
    deviceFidProvider: () -> String?,
): HttpClient = createSseEngine {
    install(SSE)
    configureContentNegotiation()
    configureLogging()
    configureDefaultRequest(baseUrl, deviceFidProvider)
    configureBearerAuthSse(tokenManager)
    handleErrors()
}

private fun HttpClientConfig<*>.configureContentNegotiation() {
    install(ContentNegotiation) {
        json(jsonConfig)
    }
}

private fun HttpClientConfig<*>.configureLogging() {
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = Napier.d(message, tag = "Ktor")
        }
        level = LogLevel.BODY
    }
}

private fun HttpClientConfig<*>.configureTimeout() {
    install(HttpTimeout) {
        connectTimeoutMillis = CONNECT_TIMEOUT_MS
        socketTimeoutMillis = SOCKET_TIMEOUT_MS
        requestTimeoutMillis = REQUEST_TIMEOUT_MS
    }
}

private fun HttpClientConfig<*>.configureDefaultRequest(
    baseUrl: String,
    deviceFidProvider: () -> String?,
) {
    defaultRequest {
        url(baseUrl)
        deviceFidProvider()?.let { fid ->
            headers.append("device-fid", fid)
        }
    }
}

private fun HttpClientConfig<*>.configureBearerAuth(
    tokenManager: TokenManager,
    authRepositoryProvider: () -> AuthRepository,
    tokenCache: TokenCache,
) {
    install(Auth) {
        bearer {
            loadTokens {
                tokenCache.value ?: tokenManager.currentTokens?.let {
                    BearerTokens(it.accessToken, it.refreshToken).also { t -> tokenCache.value = t }
                }
            }
            refreshTokens {
                val storedRefreshToken = oldTokens?.refreshToken
                val currentRefreshToken = tokenManager.currentTokens?.refreshToken
                if (storedRefreshToken != currentRefreshToken) {
                    throw ApiException.Auth
                }
                val refreshToken = storedRefreshToken ?: throw ApiException.Auth
                authRepositoryProvider()
                    .requestTokens(refreshToken)
                    .getOrElse { throw ApiException.Auth }
                    .let { BearerTokens(it.accessToken, it.refreshToken).also { t -> tokenCache.value = t } }
            }
        }
    }
}

private fun HttpClientConfig<*>.configureBearerAuthSse(tokenManager: TokenManager) {
    install(Auth) {
        bearer {
            loadTokens {
                tokenManager.currentTokens?.let {
                    BearerTokens(it.accessToken, it.refreshToken)
                }
            }
        }
    }
}

private fun HttpClientConfig<*>.handleErrors() {
    install(HttpCallValidator) {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                val error = runCatching { response.body<ErrorResponse>() }.getOrNull()
                if (error != null) {
                    if (response.status.value == 401) throw ApiException.Auth
                    throw ApiException.Error(error.tag.toNetworkError())
                } else if (response.status.value in 500..599) {
                    throw ApiException.Network
                }
            }
        }
    }
}
