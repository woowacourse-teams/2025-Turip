package com.on.turip.core.local.di

import com.on.turip.core.domain.repository.SearchHistoryRepository
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.local.searchhistory.DefaultSearchHistoryRepository
import com.on.turip.core.local.userstorage.DefaultTokenManager
import com.on.turip.core.local.userstorage.DefaultUserStorageRepository
import org.koin.dsl.module

val localModule = module {
    single<UserStorageRepository> { DefaultUserStorageRepository(get()) }
    single<SearchHistoryRepository> { DefaultSearchHistoryRepository(get()) }
    single<TokenManager> { DefaultTokenManager(get(), get()) }
}
