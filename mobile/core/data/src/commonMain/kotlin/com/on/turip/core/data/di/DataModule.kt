package com.on.turip.core.data.di

import com.on.turip.core.data.repository.DefaultAccountRepository
import com.on.turip.core.data.repository.DefaultAuthRepository
import com.on.turip.core.data.repository.DefaultBookmarkRepository
import com.on.turip.core.data.repository.DefaultContentRepository
import com.on.turip.core.data.repository.DefaultDeferredDeepLinkRepository
import com.on.turip.core.data.repository.DefaultGuestRepository
import com.on.turip.core.data.repository.DefaultMemberRepository
import com.on.turip.core.data.repository.DefaultRegionRepository
import com.on.turip.core.data.repository.DefaultSearchHistoryRepository
import com.on.turip.core.data.repository.DefaultTuripRepository
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.data.userstorage.DefaultTokenManager
import com.on.turip.core.data.userstorage.DefaultUserStorageRepository
import com.on.turip.core.domain.repository.AccountRepository
import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.DeferredDeepLinkRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.domain.repository.SearchHistoryRepository
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.domain.usecase.DeleteTuripUseCase
import com.on.turip.core.domain.usecase.DetermineInitialSessionUseCase
import com.on.turip.core.domain.usecase.DetermineInvitationEntryRouteUseCase
import com.on.turip.core.domain.usecase.ObserveTuripStreamUseCase
import com.on.turip.core.domain.usecase.TuripStreamHeartbeatManager
import com.on.turip.core.domain.usecase.UpdateBookmarkUseCase
import com.on.turip.domain.login.GuestRepository
import com.on.turip.domain.login.MemberRepository
import org.koin.dsl.module

val dataModule = module {
    single<AccountRepository> { DefaultAccountRepository(get()) }
    single<AuthRepository> { DefaultAuthRepository(get(), get()) }
    single<BookmarkRepository> { DefaultBookmarkRepository(get(), get()) }
    single<ContentRepository> { DefaultContentRepository(get()) }
    single<DeferredDeepLinkRepository> { DefaultDeferredDeepLinkRepository(get(), get()) }
    single<GuestRepository> { DefaultGuestRepository(get()) }
    single<MemberRepository> { DefaultMemberRepository(get()) }
    single<RegionRepository> { DefaultRegionRepository(get()) }
    single<TuripRepository> { DefaultTuripRepository(get(), get()) }
    single<UserStorageRepository> { DefaultUserStorageRepository(get()) }
    single<SearchHistoryRepository> { DefaultSearchHistoryRepository(get()) }
    single<TokenManager> { DefaultTokenManager(get(), get()) }
    single<SessionManager> { SessionManager(get(), get(), get()) }
    single<DeleteTuripUseCase> { DeleteTuripUseCase(get()) }
    single<DetermineInvitationEntryRouteUseCase> { DetermineInvitationEntryRouteUseCase(get()) }
    single<DetermineInitialSessionUseCase> { DetermineInitialSessionUseCase(get(), get()) }
    single<UpdateBookmarkUseCase> { UpdateBookmarkUseCase(get()) }
    single<TuripStreamHeartbeatManager> { TuripStreamHeartbeatManager() }
    single<ObserveTuripStreamUseCase> { ObserveTuripStreamUseCase(get(), get()) }
}
