package com.on.turip.core.network.di

import com.on.turip.core.domain.turip.TuripStreamService
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val datasourceModule = module {
    single<AuthDatasource> { DefaultAuthDatasource(get()) }
    single<TuripDatasource> { DefaultTuripDatasource(get()) }
    single<ContentDatasource> { DefaultContentDatasource(get()) }
    single<BookmarkDatasource> { DefaultBookmarkDatasource(get()) }
    single<RegionDatasource> { DefaultRegionDatasource(get()) }
    single<AccountDatasource> { DefaultAccountDatasource(get()) }
    single<TuripStreamService> { DefaultTuripStreamService(get<HttpClient>(named(SSE_HTTP_CLIENT))) }
}
