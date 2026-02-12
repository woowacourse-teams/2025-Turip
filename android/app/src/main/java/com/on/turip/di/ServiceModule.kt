package com.on.turip.di

import com.on.turip.data.bookmarks.service.BookmarkService
import com.on.turip.data.bookmarks.service.createBookmarkService
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
    fun provideContentService(ktorfit: Ktorfit): ContentService = ktorfit.createContentService()

    @Provides
    @Singleton
    fun provideBookmarkService(ktorfit: Ktorfit): BookmarkService = ktorfit.createBookmarkService()

    @Provides
    @Singleton
    fun provideTuripService(ktorfit: Ktorfit): TuripService = ktorfit.createTuripService()

    @Provides
    @Singleton
    fun provideRegionService(ktorfit: Ktorfit): RegionService = ktorfit.createRegionService()

    @Provides
    @Singleton
    fun provideAuthService(ktorfit: Ktorfit): AuthService = ktorfit.createAuthService()

    @Provides
    @Singleton
    fun provideMemberService(ktorfit: Ktorfit): MemberService = ktorfit.createMemberService()

    @Provides
    @Singleton
    fun provideGuestService(ktorfit: Ktorfit): GuestService = ktorfit.createGuestService()
}
