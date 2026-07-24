package com.on.turip.di

import com.on.turip.BuildConfig
import com.on.turip.common.FidProvider
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.fold
import com.on.turip.data.result.ApiException
import com.on.turip.di.NetworkModule.LOG_PREFIX
import com.on.turip.di.qualifier.DefaultHttpClient
import com.on.turip.di.qualifier.DefaultKtorfit
import com.on.turip.di.qualifier.NoAuthHttpClient
import com.on.turip.di.qualifier.NoAuthKtorfit
import com.on.turip.di.qualifier.SseHttpClient
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.session.TokenManager
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig.Companion.INFINITE_TIMEOUT_MS
import io.ktor.client.plugins.auth.Auth
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
import timber.log.Timber
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val LOG_PREFIX = "moongjenut"
    private const val CONNECT_TIMEOUT_MILLIS = 10_000L
    private const val SOCKET_TIMEOUT_MILLIS = 20_000L
    private const val REQUEST_TIMEOUT_MILLIS = 20_000L

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }

    @Provides
    @Singleton
    @DefaultHttpClient
    fun provideDefaultHttpClient(
        tokenManager: TokenManager,
        authRepository: Lazy<AuthRepository>,
        fidProvider: FidProvider,
        json: Json,
    ): HttpClient =
        HttpClient(OkHttp) {
            /**
             * HTTP 응답이 성공(2xx)이 아닐 경우
             * 자동으로 예외를 발생시키기 위한 설정
             *
             * - ClientRequestException : 4xx (클라이언트 요청 오류)
             * - ServerResponseException : 5xx (서버 오류)
             * - IOException : 네트워크 오류
             *
             * 이 설정이 없으면 400 / 500 에러 응답도
             * 정상 응답처럼 JSON 파싱을 시도하게 되어
             * 의도하지 않은 파싱 오류가 발생할 수 있다.
             */
            expectSuccess = true

            timeoutInterceptor()
            loggingInterceptor()
            contentNegotiationInterceptor(json)
            defaultRequestInterceptor(fidProvider)
            headerInterceptor(tokenManager, authRepository)
        }

    @Provides
    @Singleton
    @NoAuthHttpClient
    fun provideNoAuthHttpClient(
        fidProvider: FidProvider,
        json: Json,
    ): HttpClient =
        HttpClient(OkHttp) {
            expectSuccess = true

            timeoutInterceptor()
            loggingInterceptor()
            contentNegotiationInterceptor(json)
            defaultRequestInterceptor(fidProvider)
        }

    @Provides
    @Singleton
    @SseHttpClient
    fun provideSseHttpClient(
        tokenManager: TokenManager,
        authRepository: Lazy<AuthRepository>,
        fidProvider: FidProvider,
        json: Json,
    ): HttpClient =
        HttpClient(OkHttp) {
            expectSuccess = true
            install(SSE) {
                reconnectionTime = 3000.milliseconds
            }
            install(HttpTimeout) {
                connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
                socketTimeoutMillis = INFINITE_TIMEOUT_MS
                requestTimeoutMillis = INFINITE_TIMEOUT_MS
            }
            loggingInterceptor()
            contentNegotiationInterceptor(json)
            defaultRequestInterceptor(fidProvider)
            headerInterceptor(tokenManager, authRepository)
        }

    private fun HttpClientConfig<OkHttpConfig>.timeoutInterceptor() {
        install(HttpTimeout) {
            // 서버와 TCP 연결을 맺는 시간 제한
            connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
            // 서버와 연결된 후 데이터를 읽는 동안 아무 데이터도 안 오면 기다리는 최대 시간
            socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
            // 전체 HTTP 요청이 완료될 때까지 기다리는 최대 시간
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.loggingInterceptor() {
        install(plugin = Logging) {
            logger = PrettyLogger
            level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.contentNegotiationInterceptor(json: Json) {
        install(plugin = ContentNegotiation) {
            json(json)
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.defaultRequestInterceptor(fidProvider: FidProvider) {
        defaultRequest {
            header("device-fid", fidProvider.cachedFid)
            contentType(type = ContentType.Application.Json)
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.headerInterceptor(
        tokenManager: TokenManager,
        authRepository: Lazy<AuthRepository>,
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

                    // 리프레시 토큰으로 토큰 갱신 요청 API는 header 없도록 구성되어 있음(header 있을 경우 이미 만료된 엑세스 토큰이어서 401 발생)
                    authRepository.get().requestTokens(storedRefreshToken).fold(
                        onSuccess = { newTokens: AuthTokens ->
                            Timber.d("refreshToken으로 토큰 재발급 성공")
                            val currentRefreshToken = tokenManager.currentTokens?.refreshToken

                            // 중단함수 처리 중 이미 토큰 재발급이 되었거나 제거가 발생했을 경우
                            if (currentRefreshToken != storedRefreshToken) throw ApiException.Auth

                            tokenManager.setTokens(newTokens).fold(
                                onSuccess = {
                                    Timber.d("refreshToken으로 재발급 받은 토큰 저장 성공")
                                    BearerTokens(
                                        accessToken = newTokens.accessToken,
                                        refreshToken = newTokens.refreshToken,
                                    )
                                },
                                onFailure = {
                                    Timber.e("refreshToken으로 재발급 받은 토큰 저장 실패")
                                    throw ApiException.Auth
                                },
                            )
                        },
                        onFailure = { errorType ->
                            Timber.e("refreshToken으로 토큰 재발급 실패 errorType = $errorType")
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

    @Provides
    @Singleton
    @DefaultKtorfit
    fun provideDefaultKtorfit(
        @DefaultHttpClient httpClient: HttpClient,
    ): Ktorfit =
        Ktorfit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .httpClient(httpClient)
            .build()

    @Provides
    @Singleton
    @NoAuthKtorfit
    fun provideNoAuthKtorfit(
        @NoAuthHttpClient httpClient: HttpClient,
    ): Ktorfit =
        Ktorfit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .httpClient(httpClient)
            .build()
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
        Timber.tag(LOG_PREFIX).d(message = replacedMessage)
    }

    private fun Json.prettyJson(json: String): String =
        try {
            val parsed = parseToJsonElement(json)
            encodeToString(JsonElement.serializer(), parsed)
        } catch (e: Exception) {
            Timber.tag(LOG_PREFIX).e(e)
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
