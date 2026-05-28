package com.on.turip.core.network.di

import com.on.turip.core.domain.fid.DeviceFidManager
import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.domain.session.AuthTokenCacheController
import com.on.turip.core.domain.session.TokenManager
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val DEFAULT_HTTP_CLIENT = "DefaultHttpClient"
const val NO_AUTH_HTTP_CLIENT = "NoAuthHttpClient"
const val SSE_HTTP_CLIENT = "SseHttpClient"

const val DEFAULT_KTORFIT = "DefaultKtorfit"
const val NO_AUTH_KTORFIT = "NoAuthKtorfit"

fun networkModule(baseUrl: String) = module {
    single { DefaultAuthTokenCacheController() }
    single<AuthTokenCacheController> { get<DefaultAuthTokenCacheController>() }

    single<HttpClient>(named(DEFAULT_HTTP_CLIENT)) {
        val fidManager = get<DeviceFidManager>()
        buildDefaultHttpClient(
            baseUrl = baseUrl,
            tokenManager = get<TokenManager>(),
            authRepositoryProvider = { get<AuthRepository>() },
            deviceFidProvider = { fidManager.getFid() },
            authTokenCacheController = get<DefaultAuthTokenCacheController>(),
        )
    }

    single<HttpClient>(named(NO_AUTH_HTTP_CLIENT)) {
        val fidManager = get<DeviceFidManager>()
        buildNoAuthHttpClient(
            baseUrl = baseUrl,
            deviceFidProvider = { fidManager.getFid() },
        )
    }

    single<HttpClient>(named(SSE_HTTP_CLIENT)) {
        val fidManager = get<DeviceFidManager>()
        buildSseHttpClient(
            baseUrl = baseUrl,
            tokenManager = get<TokenManager>(),
            deviceFidProvider = { fidManager.getFid() },
        )
    }

    single<Ktorfit>(named(DEFAULT_KTORFIT)) {
        Ktorfit
            .Builder()
            .baseUrl(baseUrl)
            .httpClient(get<HttpClient>(named(DEFAULT_HTTP_CLIENT)))
            .build()
    }

    single<Ktorfit>(named(NO_AUTH_KTORFIT)) {
        Ktorfit
            .Builder()
            .baseUrl(baseUrl)
            .httpClient(get<HttpClient>(named(NO_AUTH_HTTP_CLIENT)))
            .build()
    }
}
