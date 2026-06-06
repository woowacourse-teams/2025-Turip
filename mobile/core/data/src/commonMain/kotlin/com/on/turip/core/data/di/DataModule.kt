package com.on.turip.core.data.di

import com.on.turip.core.data.session.DefaultSessionManager
import com.on.turip.core.domain.session.SessionManager
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { DefaultAuthRepository(get()) }
    single<TuripRepository> { DefaultTuripRepository(get()) }
    single<ContentRepository> { DefaultContentRepository(get()) }
    single<BookmarkRepository> { DefaultBookmarkRepository(get()) }
    single<RegionRepository> { DefaultRegionRepository(get()) }
    single<InvitationRepository> { DefaultInvitationRepository(get()) }
    single<AccountRepository> { DefaultAccountRepository(get()) }
    single<SessionManager> { DefaultSessionManager(get()) }
    single<DeferredDeepLinkRepository> { DefaultDeferredDeepLinkRepository() }
}
