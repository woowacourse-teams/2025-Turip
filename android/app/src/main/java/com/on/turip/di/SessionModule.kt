package com.on.turip.di

import com.on.turip.data.session.DefaultAuthTokenCacheController
import com.on.turip.data.session.DefaultSessionStore
import com.on.turip.data.session.DefaultTokenManager
import com.on.turip.domain.session.AuthTokenCacheController
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.session.TokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
    @Binds
    @Singleton
    abstract fun bindTokenManager(defaultTokenManager: DefaultTokenManager): TokenManager

    @Binds
    @Singleton
    abstract fun bindAuthTokenCacheController(defaultAuthTokenCacheController: DefaultAuthTokenCacheController): AuthTokenCacheController

    @Binds
    @Singleton
    abstract fun bindSessionStore(defaultSessionStore: DefaultSessionStore): SessionStore
}
