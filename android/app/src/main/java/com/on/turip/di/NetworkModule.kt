package com.on.turip.di

import com.on.turip.BuildConfig
import com.on.turip.common.FidProvider
import com.on.turip.core.result.fold
import com.on.turip.di.NetworkModule.LOG_PREFIX
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
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val LOG_PREFIX = "moongjenut"

    @Provides
    @Singleton
    fun provideHttpClient(
        tokenManager: TokenManager,
        authRepository: Lazy<AuthRepository>,
        fidProvider: FidProvider,
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

            loggingInterceptor()

            install(plugin = ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                    },
                )
            }

            headerInterceptor(tokenManager, authRepository, fidProvider)
        }

    private fun HttpClientConfig<OkHttpConfig>.headerInterceptor(
        tokenManager: TokenManager,
        authRepository: Lazy<AuthRepository>,
        fidProvider: FidProvider,
    ) {
        install(plugin = Auth) {
            bearer {
                loadTokens {
                    tokenManager.currentTokens()?.let { tokens: AuthTokens ->
                        BearerTokens(
                            accessToken = tokens.accessToken,
                            refreshToken = tokens.refreshToken,
                        )
                    }
                }

                refreshTokens {
                    val storedRefreshToken: String =
                        tokenManager.currentTokens()?.refreshToken
                            ?: return@refreshTokens null

                    return@refreshTokens authRepository
                        .get()
                        .requestTokens(storedRefreshToken)
                        .fold(
                            onSuccess = { newTokens: AuthTokens ->
                                tokenManager.setTokens(newTokens)
                                BearerTokens(
                                    accessToken = newTokens.accessToken,
                                    refreshToken = newTokens.refreshToken,
                                )
                            },
                            onFailure = {
                                // #591 이슈에서 처리 필요
                                null
                            },
                        )
                }
            }
        }

        defaultRequest {
            header("device-fid", fidProvider.cachedFid)
            contentType(type = ContentType.Application.Json)
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.loggingInterceptor() {
        install(plugin = Logging) {
            logger = PrettyLogger
            level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
        }
    }

    @Provides
    @Singleton
    fun provideKtorfit(httpClient: HttpClient): Ktorfit =
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
