package com.on.turip.di

import com.on.turip.data.bookmarks.service.BookmarkService
import com.on.turip.data.content.service.ContentService
import com.on.turip.data.login.service.AuthService
import com.on.turip.data.login.service.MemberService
import com.on.turip.data.region.service.RegionService
import com.on.turip.data.turip.service.TuripService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideContentService(retrofit: Retrofit): ContentService = retrofit.create<ContentService>()

    @Provides
    @Singleton
    fun provideBookmarkService(retrofit: Retrofit): BookmarkService = retrofit.create<BookmarkService>()

    @Provides
    @Singleton
    fun provideTuripService(retrofit: Retrofit): TuripService = retrofit.create<TuripService>()

    @Provides
    @Singleton
    fun provideRegionService(retrofit: Retrofit): RegionService = retrofit.create<RegionService>()

    @Provides
    @Singleton
    fun provideAuthService(
        @Named("refreshRetrofit") retrofit: Retrofit,
    ): AuthService = retrofit.create<AuthService>()

    @Provides
    @Singleton
    fun provideMemberService(retrofit: Retrofit): MemberService = retrofit.create<MemberService>()
}
