package com.on.turip.core.network.di

import com.on.turip.core.data.datasource.AccountRemoteDataSource
import com.on.turip.core.data.datasource.AuthRefreshRemoteDataSource
import com.on.turip.core.data.datasource.AuthRemoteDataSource
import com.on.turip.core.data.datasource.BookmarkRemoteDataSource
import com.on.turip.core.data.datasource.ContentRemoteDataSource
import com.on.turip.core.data.datasource.GuestRemoteDataSource
import com.on.turip.core.data.datasource.MemberRemoteDataSource
import com.on.turip.core.data.datasource.RegionRemoteDataSource
import com.on.turip.core.data.datasource.TuripRemoteDataSource
import com.on.turip.core.data.datasource.TuripSseStreamDataSource
import com.on.turip.core.network.datasourceimpl.DefaultAccountRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultAuthRefreshRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultAuthRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultBookmarkRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultContentRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultGuestRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultMemberRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultRegionRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultTuripRemoteDataSource
import com.on.turip.core.network.datasourceimpl.DefaultTuripSseStreamDataSource
import com.on.turip.core.network.sse.TuripSseParser
import com.on.turip.core.network.service.DefaultTuripStreamService
import com.on.turip.core.network.service.TuripStreamService
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun datasourceModule(baseUrl: String) = module {
    single<AccountRemoteDataSource> { DefaultAccountRemoteDataSource(accountService = get()) }
    single<AuthRemoteDataSource> {
        DefaultAuthRemoteDataSource(authService = get(named(DEFAULT_AUTH_SERVICE)))
    }
    single<AuthRefreshRemoteDataSource> {
        DefaultAuthRefreshRemoteDataSource(authService = get(named(NO_AUTH_AUTH_SERVICE)))
    }
    single<BookmarkRemoteDataSource> { DefaultBookmarkRemoteDataSource(bookmarkService = get()) }
    single<ContentRemoteDataSource> { DefaultContentRemoteDataSource(contentService = get()) }
    single<GuestRemoteDataSource> { DefaultGuestRemoteDataSource(guestService = get()) }
    single<MemberRemoteDataSource> { DefaultMemberRemoteDataSource(memberService = get()) }
    single<RegionRemoteDataSource> { DefaultRegionRemoteDataSource(regionService = get()) }
    single<TuripRemoteDataSource> { DefaultTuripRemoteDataSource(turipService = get()) }
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
    single { TuripSseParser(json = get()) }
    single<TuripStreamService> {
        DefaultTuripStreamService(
            httpClient = get(named(SSE_HTTP_CLIENT)),
            baseUrl = baseUrl,
        )
    }
    single<TuripSseStreamDataSource> {
        DefaultTuripSseStreamDataSource(
            turipStreamService = get(),
            parser = get(),
        )
    }
}
