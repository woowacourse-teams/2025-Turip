package com.on.turip.core.data.di

import com.on.turip.core.data.repository.DefaultAccountRepository
import com.on.turip.core.data.repository.DefaultAuthRepository
import com.on.turip.core.data.repository.DefaultBookmarkRepository
import com.on.turip.core.data.repository.DefaultContentRepository
import com.on.turip.core.data.repository.DefaultDeferredDeepLinkRepository
import com.on.turip.core.data.repository.DefaultInvitationRepository
import com.on.turip.core.data.repository.DefaultRegionRepository
import com.on.turip.core.data.session.DefaultSessionManager
import com.on.turip.core.data.repository.DefaultTuripRepository
import com.on.turip.core.domain.repository.AccountRepository
import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.DeferredDeepLinkRepository
import com.on.turip.core.domain.repository.InvitationRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.domain.session.SessionManager
import com.on.turip.core.domain.repository.TuripRepository
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
