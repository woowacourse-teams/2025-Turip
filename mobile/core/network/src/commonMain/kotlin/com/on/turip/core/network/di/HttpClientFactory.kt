package com.on.turip.core.network.di

import com.on.turip.core.common.ApiException
import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.model.login.AuthTokens
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.fold
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig.Companion.INFINITE_TIMEOUT_MS
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration.Companion.milliseconds

private const val LOG_PREFIX = "moongjenut"
private const val CONNECT_TIMEOUT_MILLIS = 10_000L
private const val SOCKET_TIMEOUT_MILLIS = 20_000L
private const val REQUEST_TIMEOUT_MILLIS = 20_000L

private val jsonConfig = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}

expect fun createDefaultEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient

expect fun createNoAuthEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient

expect fun createSseEngine(config: HttpClientConfig<*>.() -> Unit): HttpClient

fun buildDefaultHttpClient(
    baseUrl: String,
    isDebug: Boolean,
    tokenManager: TokenManager,
    authRepositoryProvider: () -> AuthRepository,
    deviceFidProvider: () -> String?,
    authTokenCacheController: DefaultAuthTokenCacheController,
): HttpClient = createDefaultEngine {
    expectSuccess = true

    timeoutInterceptor()
    loggingInterceptor(isDebug)
    contentNegotiationInterceptor()
    defaultRequestInterceptor(baseUrl, deviceFidProvider)
    headerInterceptor(tokenManager, authRepositoryProvider)
}.registerBearerTokenClear(authTokenCacheController)

fun buildNoAuthHttpClient(
    baseUrl: String,
    isDebug: Boolean,
    deviceFidProvider: () -> String?,
): HttpClient = createNoAuthEngine {
    expectSuccess = true

    timeoutInterceptor()
    loggingInterceptor(isDebug)
    contentNegotiationInterceptor()
    defaultRequestInterceptor(baseUrl, deviceFidProvider)
}

fun buildSseHttpClient(
    baseUrl: String,
    isDebug: Boolean,
    tokenManager: TokenManager,
    authRepositoryProvider: () -> AuthRepository,
    deviceFidProvider: () -> String?,
    authTokenCacheController: DefaultAuthTokenCacheController,
): HttpClient = createSseEngine {
    expectSuccess = true
    install(SSE) {
        reconnectionTime = 3000.milliseconds
    }
    install(HttpTimeout) {
        connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
        socketTimeoutMillis = INFINITE_TIMEOUT_MS
        requestTimeoutMillis = INFINITE_TIMEOUT_MS
    }
    loggingInterceptor(isDebug)
    contentNegotiationInterceptor()
    defaultRequestInterceptor(baseUrl, deviceFidProvider)
    headerInterceptor(tokenManager, authRepositoryProvider)
}.registerBearerTokenClear(authTokenCacheController)

private fun HttpClientConfig<*>.timeoutInterceptor() {
    install(HttpTimeout) {
        // 서버와 TCP 연결을 맺는 시간 제한
        connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
        // 서버와 연결된 후 데이터를 읽는 동안 아무 데이터도 안 오면 기다리는 최대 시간
        socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
        // 전체 HTTP 요청이 완료될 때까지 기다리는 최대 시간
        requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
    }
}

private fun HttpClientConfig<*>.loggingInterceptor(isDebug: Boolean) {
    install(plugin = Logging) {
        logger = PrettyLogger
        level = if (isDebug) LogLevel.ALL else LogLevel.NONE
    }
}

private fun HttpClientConfig<*>.contentNegotiationInterceptor() {
    install(plugin = ContentNegotiation) {
        json(jsonConfig)
    }
}

private fun HttpClientConfig<*>.defaultRequestInterceptor(
    baseUrl: String,
    deviceFidProvider: () -> String?,
) {
    defaultRequest {
        url(baseUrl)
        deviceFidProvider()?.let { fid ->
            header("device-fid", fid)
        }
        contentType(ContentType.Application.Json)
    }
}

private fun HttpClientConfig<*>.headerInterceptor(
    tokenManager: TokenManager,
    authRepositoryProvider: () -> AuthRepository,
) {
    install(plugin = Auth) {
        bearer {
            loadTokens {
                tokenManager.currentTokens?.let { tokens: AuthTokens ->
                    BearerTokens(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken,
                    )
                }
            }

            refreshTokens {
                val storedRefreshToken: String =
                    tokenManager.currentTokens?.refreshToken ?: throw ApiException.Auth

                // HTTP/SSE 두 클라이언트가 동시에 401을 받아도 TokenManager가 갱신을 직렬화한다.
                // 리프레시 토큰으로 토큰 갱신 요청 API는 header 없도록 구성되어 있음(header 있을 경우 이미 만료된 엑세스 토큰이어서 401 발생)
                tokenManager
                    .refreshTokens(storedRefreshToken) { refreshToken ->
                        authRepositoryProvider().requestTokens(refreshToken)
                    }.fold(
                        onSuccess = { newTokens: AuthTokens ->
                            Napier.d("refreshToken으로 토큰 재발급 성공")
                            BearerTokens(
                                accessToken = newTokens.accessToken,
                                refreshToken = newTokens.refreshToken,
                            )
                        },
                        onFailure = { errorType ->
                            Napier.e("refreshToken으로 토큰 재발급 실패 errorType = $errorType")
                            when (errorType) {
                                ErrorType.Network -> throw ApiException.Network
                                is ErrorType.Auth -> throw ApiException.Auth
                                else -> throw ApiException.Error(errorType)
                            }
                        },
                    )
            }
        }
    }
}

/**
 * 토큰 무효화 시점에 Ktor [BearerAuthProvider]가 메모리에 캐싱한 토큰을 비우도록 등록한다.
 * 재로그인(계정 전환) 후에도 이전 계정 토큰이 재사용되는 문제를 막는다.
 */
private fun HttpClient.registerBearerTokenClear(
    authTokenCacheController: DefaultAuthTokenCacheController,
): HttpClient =
    apply {
        authTokenCacheController.registerOnClear {
            authProvider<BearerAuthProvider>()?.clearToken()
        }
    }

@OptIn(ExperimentalSerializationApi::class)
private object PrettyLogger : Logger {
    private val jsonConfiguration =
        Json {
            prettyPrintIndent = "\t"
            prettyPrint = true
        }

    override fun log(message: String) {
        val replacedMessage: String = replaceBodyWithPrettyJson(message)
        Napier.d(message = replacedMessage, tag = LOG_PREFIX)
    }

    private fun Json.prettyJson(json: String): String =
        try {
            val parsed = parseToJsonElement(json)
            encodeToString(JsonElement.serializer(), parsed)
        } catch (e: Exception) {
            Napier.e(throwable = e, tag = LOG_PREFIX, message = "")
            json
        }

    private fun replaceBodyWithPrettyJson(message: String): String {
        val startToken = "BODY START"
        val endToken = "BODY END"

        val startIndex: Int = message.indexOf(startToken)
        val endIndex: Int = message.indexOf(endToken)

        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            return message
        }

        val bodyStart: Int = startIndex + startToken.length
        val rawBody: String = message.substring(bodyStart, endIndex).trim()

        val prettyBody: String =
            if (rawBody.isBlank()) {
                rawBody
            } else {
                jsonConfiguration.prettyJson(rawBody)
            }

        val before: String = message.take(bodyStart)
        val after: String = message.substring(endIndex)

        return buildString {
            appendLine("----------".repeat(10))
            append(before)
            append("\n")
            append(prettyBody.prependIndent("\t"))
            append("\n")
            appendLine(after)
            appendLine("----------".repeat(10))
        }
    }
}
