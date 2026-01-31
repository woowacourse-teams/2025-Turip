package com.on.turip.di

import com.on.turip.data.bookmarks.repository.DefaultBookmarkRepository
import com.on.turip.data.content.repository.DefaultContentRepository
import com.on.turip.data.login.repository.DefaultAuthRepository
import com.on.turip.data.login.repository.DefaultGuestRepository
import com.on.turip.data.login.repository.DefaultMemberRepository
import com.on.turip.data.region.repository.DefaultRegionRepository
import com.on.turip.data.searchhistory.repository.DefaultSearchHistoryRepository
import com.on.turip.data.turip.repository.DefaultTuripRepository
import com.on.turip.data.userstorage.repository.DefaultUserStorageRepository
import com.on.turip.domain.bookmark.repository.BookmarkRepository
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.login.GuestRepository
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.region.repository.RegionRepository
import com.on.turip.domain.searchhistory.SearchHistoryRepository
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindContentRepository(defaultContentRepository: DefaultContentRepository): ContentRepository

    @Binds
    @Singleton
    abstract fun bindRegionRepository(defaultRegionRepository: DefaultRegionRepository): RegionRepository

    @Binds
    @Singleton
    abstract fun bindUserStorageRepository(defaultUserStorageRepository: DefaultUserStorageRepository): UserStorageRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(defaultBookmarkRepository: DefaultBookmarkRepository): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(defaultSearchHistoryRepository: DefaultSearchHistoryRepository): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindTuripRepository(defaultTuripRepository: DefaultTuripRepository): TuripRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(defaultAuthRepository: DefaultAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMemberRepository(defaultMemberRepository: DefaultMemberRepository): MemberRepository

    @Binds
    @Singleton
    abstract fun bindGuestRepository(defaultGuestRepository: DefaultGuestRepository): GuestRepository
}
