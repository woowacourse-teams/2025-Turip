package com.on.turip.di

import com.on.turip.data.account.service.AccountService
import com.on.turip.data.account.service.createAccountService
import com.on.turip.data.bookmark.service.BookmarkService
import com.on.turip.data.bookmark.service.createBookmarkService
import com.on.turip.data.content.service.ContentService
import com.on.turip.data.content.service.createContentService
import com.on.turip.data.login.service.AuthService
import com.on.turip.data.login.service.GuestService
import com.on.turip.data.login.service.MemberService
import com.on.turip.data.login.service.createAuthService
import com.on.turip.data.login.service.createGuestService
import com.on.turip.data.login.service.createMemberService
import com.on.turip.data.region.service.RegionService
import com.on.turip.data.region.service.createRegionService
import com.on.turip.data.turip.service.TuripService
import com.on.turip.data.turip.service.createTuripService
import com.on.turip.di.qualifier.DefaultAuthService
import com.on.turip.di.qualifier.DefaultKtorfit
import com.on.turip.di.qualifier.NoAuthAuthService
import com.on.turip.di.qualifier.NoAuthKtorfit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideContentService(
        @DefaultKtorfit ktorfit: Ktorfit,
    ): ContentService = ktorfit.createContentService()

    @Provides
    @Singleton
    fun provideBookmarkService(
        @DefaultKtorfit ktorfit: Ktorfit,
    ): BookmarkService = ktorfit.createBookmarkService()

    @Provides
    @Singleton
    fun provideTuripService(
        @DefaultKtorfit ktorfit: Ktorfit,
    ): TuripService = ktorfit.createTuripService()

    @Provides
    @Singleton
    fun provideRegionService(
        @DefaultKtorfit ktorfit: Ktorfit,
    ): RegionService = ktorfit.createRegionService()

    @Provides
    @Singleton
    @DefaultAuthService
    fun provideDefaultAuthService(
        @DefaultKtorfit ktorfit: Ktorfit,
    ): AuthService = ktorfit.createAuthService()

    @Provides
    @Singleton
    @NoAuthAuthService
    fun provideNoAuthAuthService(
        @NoAuthKtorfit ktorfit: Ktorfit,
    ): AuthService = ktorfit.createAuthService()

    @Provides
    @Singleton
    fun provideMemberService(
        @DefaultKtorfit ktorfit: Ktorfit,
    ): MemberService = ktorfit.createMemberService()

    @Provides
    @Singleton
    fun provideGuestService(
        @DefaultKtorfit ktorfit: Ktorfit,
    ): GuestService = ktorfit.createGuestService()

    @Provides
    @Singleton
    fun provideAccountService(
        @DefaultKtorfit ktorfit: Ktorfit,
    ): AccountService = ktorfit.createAccountService()
}
