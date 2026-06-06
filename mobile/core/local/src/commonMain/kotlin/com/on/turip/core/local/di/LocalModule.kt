package com.on.turip.core.local.di

import com.on.turip.core.domain.fid.DeviceFidManager
import com.on.turip.core.domain.repository.SearchHistoryRepository
import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.local.datasource.DeferredDeepLinkLocalDataSource
import com.on.turip.core.local.datasource.UserStorageLocalDataSource
import com.on.turip.core.local.datasourceimpl.DefaultDeferredDeepLinkLocalDataSource
import com.on.turip.core.local.datasourceimpl.DefaultUserStorageLocalDataSource
import com.on.turip.core.local.fid.DefaultDeviceFidManager
import com.on.turip.core.local.searchhistory.DefaultSearchHistoryRepository
import com.on.turip.core.local.userstorage.DefaultTokenManager
import com.on.turip.core.local.userstorage.DefaultUserStorageRepository
import org.koin.dsl.module

val localModule = module {
    single<UserStorageLocalDataSource> { DefaultUserStorageLocalDataSource(get()) }
    single<DeferredDeepLinkLocalDataSource> { DefaultDeferredDeepLinkLocalDataSource(get()) }
    single<UserStorageRepository> { DefaultUserStorageRepository(get()) }
    single<SearchHistoryRepository> { DefaultSearchHistoryRepository(get()) }
    single<TokenManager> { DefaultTokenManager(get(), get()) }
    single<DeviceFidManager> { DefaultDeviceFidManager(get()) }
}
