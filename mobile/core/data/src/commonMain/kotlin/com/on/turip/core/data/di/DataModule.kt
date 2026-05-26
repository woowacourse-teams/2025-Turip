package com.on.turip.core.data.di

import com.on.turip.core.data.account.DefaultAccountRepository
import com.on.turip.core.data.auth.DefaultAuthRepository
import com.on.turip.core.data.bookmark.DefaultBookmarkRepository
import com.on.turip.core.data.content.DefaultContentRepository
import com.on.turip.core.data.invitation.DefaultDeferredDeepLinkRepository
import com.on.turip.core.data.invitation.DefaultInvitationRepository
import com.on.turip.core.data.region.DefaultRegionRepository
import com.on.turip.core.data.session.DefaultSessionManager
import com.on.turip.core.data.turip.DefaultTuripRepository
import com.on.turip.core.domain.account.AccountRepository
import com.on.turip.core.domain.auth.AuthRepository
import com.on.turip.core.domain.bookmark.BookmarkRepository
import com.on.turip.core.domain.content.ContentRepository
import com.on.turip.core.domain.invitation.DeferredDeepLinkRepository
import com.on.turip.core.domain.invitation.InvitationRepository
import com.on.turip.core.domain.region.RegionRepository
import com.on.turip.core.domain.session.SessionManager
import com.on.turip.core.domain.turip.TuripRepository
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
