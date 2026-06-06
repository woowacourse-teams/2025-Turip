package com.on.turip.core.data.di

import com.on.turip.core.data.searchhistory.DefaultSearchHistoryRepository
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.data.userstorage.DefaultTokenManager
import com.on.turip.core.data.userstorage.DefaultUserStorageRepository
import com.on.turip.core.domain.repository.SearchHistoryRepository
import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.domain.session.TokenManager
import org.koin.dsl.module

val dataModule = module {
    single<UserStorageRepository> { DefaultUserStorageRepository(get()) }
    single<SearchHistoryRepository> { DefaultSearchHistoryRepository(get()) }
    single<TokenManager> { DefaultTokenManager(get(), get()) }
    single<SessionManager> { SessionManager(get(), get(), get()) }
}
