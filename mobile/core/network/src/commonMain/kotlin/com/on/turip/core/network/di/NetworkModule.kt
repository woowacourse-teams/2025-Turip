package com.on.turip.core.network.di

import com.on.turip.core.domain.auth.AuthRepository
import com.on.turip.core.domain.session.AuthTokenCacheController
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.domain.turip.TuripStreamService
import com.on.turip.core.network.datasource.AccountDatasource
import com.on.turip.core.network.datasource.AuthDatasource
import com.on.turip.core.network.datasource.BookmarkDatasource
import com.on.turip.core.network.datasource.ContentDatasource
import com.on.turip.core.network.datasource.DefaultAccountDatasource
import com.on.turip.core.network.datasource.DefaultAuthDatasource
import com.on.turip.core.network.datasource.DefaultBookmarkDatasource
import com.on.turip.core.network.datasource.DefaultContentDatasource
import com.on.turip.core.network.datasource.DefaultRegionDatasource
import com.on.turip.core.network.datasource.DefaultTuripDatasource
import com.on.turip.core.network.datasource.DefaultTuripStreamService
import com.on.turip.core.network.datasource.RegionDatasource
import com.on.turip.core.network.datasource.TuripDatasource
import com.on.turip.core.network.service.AuthService
import com.on.turip.core.network.service.BookmarkService
import com.on.turip.core.network.service.ContentService
import com.on.turip.core.network.service.MemberService
import com.on.turip.core.network.service.RegionService
import com.on.turip.core.network.service.TuripService
import com.on.turip.core.network.service.createAuthService
import com.on.turip.core.network.service.createBookmarkService
import com.on.turip.core.network.service.createContentService
import com.on.turip.core.network.service.createMemberService
import com.on.turip.core.network.service.createRegionService
import com.on.turip.core.network.service.createTuripService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val DEFAULT_HTTP_CLIENT = "DefaultHttpClient"
const val NO_AUTH_HTTP_CLIENT = "NoAuthHttpClient"
const val SSE_HTTP_CLIENT = "SseHttpClient"

const val DEFAULT_KTORFIT = "DefaultKtorfit"
const val NO_AUTH_KTORFIT = "NoAuthKtorfit"

fun networkModule(baseUrl: String, deviceFidProvider: () -> String?) = module {
    single { DefaultAuthTokenCacheController() }
    single<AuthTokenCacheController> { get<DefaultAuthTokenCacheController>() }

    single<HttpClient>(named(DEFAULT_HTTP_CLIENT)) {
        buildDefaultHttpClient(
            baseUrl = baseUrl,
            tokenManager = get<TokenManager>(),
            authRepositoryProvider = { get<AuthRepository>() },
            deviceFidProvider = deviceFidProvider,
            authTokenCacheController = get<DefaultAuthTokenCacheController>(),
        )
    }

    single<HttpClient>(named(NO_AUTH_HTTP_CLIENT)) {
        buildNoAuthHttpClient(
            baseUrl = baseUrl,
            deviceFidProvider = deviceFidProvider,
        )
    }

    single<HttpClient>(named(SSE_HTTP_CLIENT)) {
        buildSseHttpClient(
            baseUrl = baseUrl,
            tokenManager = get<TokenManager>(),
            deviceFidProvider = deviceFidProvider,
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

    single<AuthService> { get<Ktorfit>(named(NO_AUTH_KTORFIT)).createAuthService() }
    single<MemberService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createMemberService() }
    single<TuripService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createTuripService() }
    single<ContentService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createContentService() }
    single<BookmarkService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createBookmarkService() }
    single<RegionService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createRegionService() }

    single<AuthDatasource> { DefaultAuthDatasource(get()) }
    single<TuripDatasource> { DefaultTuripDatasource(get()) }
    single<ContentDatasource> { DefaultContentDatasource(get()) }
    single<BookmarkDatasource> { DefaultBookmarkDatasource(get()) }
    single<RegionDatasource> { DefaultRegionDatasource(get()) }
    single<AccountDatasource> { DefaultAccountDatasource(get()) }
    single<TuripStreamService> { DefaultTuripStreamService(get(named(SSE_HTTP_CLIENT))) }
}
