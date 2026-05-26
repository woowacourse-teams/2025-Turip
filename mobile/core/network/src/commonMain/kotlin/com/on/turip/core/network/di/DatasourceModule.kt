package com.on.turip.core.network.di

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
