package com.on.turip.di

import com.on.turip.data.account.datasource.AccountRemoteDataSource
import com.on.turip.data.account.datasource.DefaultAccountRemoteDataSource
import com.on.turip.data.bookmark.datasource.BookmarkRemoteDataSource
import com.on.turip.data.bookmark.datasource.DefaultBookmarkRemoteDataSource
import com.on.turip.data.content.datasource.ContentRemoteDataSource
import com.on.turip.data.content.datasource.DefaultContentRemoteDataSource
import com.on.turip.data.invitation.datasource.DefaultDeferredDeepLinkLocalDataSource
import com.on.turip.data.invitation.datasource.DefaultInstallReferrerDataSource
import com.on.turip.data.invitation.datasource.DeferredDeepLinkLocalDataSource
import com.on.turip.data.invitation.datasource.InstallReferrerDataSource
import com.on.turip.data.login.datasource.AuthDataSource
import com.on.turip.data.login.datasource.AuthRemoteDataSource
import com.on.turip.data.login.datasource.GuestDataSource
import com.on.turip.data.login.datasource.GuestRemoteDataSource
import com.on.turip.data.login.datasource.MemberDataSource
import com.on.turip.data.login.datasource.MemberRemoteDataSource
import com.on.turip.data.region.datasource.DefaultRegionRemoteDataSource
import com.on.turip.data.region.datasource.RegionRemoteDataSource
import com.on.turip.data.searchhistory.datasource.DefaultSearchHistoryDataSource
import com.on.turip.data.searchhistory.datasource.SearchHistoryDataSource
import com.on.turip.data.turip.datasource.DefaultTuripRemoteDataSource
import com.on.turip.data.turip.datasource.DefaultTuripSseStreamDataSource
import com.on.turip.data.turip.datasource.TuripRemoteDataSource
import com.on.turip.data.turip.datasource.TuripSseStreamDataSource
import com.on.turip.data.userstorage.datasource.DefaultUserStorageLocalDataSource
import com.on.turip.data.userstorage.datasource.UserStorageLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindContentRemoteDataSource(defaultContentRemoteDataSource: DefaultContentRemoteDataSource): ContentRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserStorageLocalDataSource(
        defaultUserStorageLocalDataSource: DefaultUserStorageLocalDataSource,
    ): UserStorageLocalDataSource

    @Binds
    @Singleton
    abstract fun bindBookmarkRemoteDataSource(defaultBookmarkRemoteDataSource: DefaultBookmarkRemoteDataSource): BookmarkRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRegionRemoteDataSource(defaultRegionRemoteDataSource: DefaultRegionRemoteDataSource): RegionRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindSearchHistoryDataSource(defaultSearchHistoryDataSource: DefaultSearchHistoryDataSource): SearchHistoryDataSource

    @Binds
    @Singleton
    abstract fun bindTuripRemoteDataSource(defaultTuripRemoteDataSource: DefaultTuripRemoteDataSource): TuripRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTuripSseStreamDataSource(defaultTuripSseStreamDataSource: DefaultTuripSseStreamDataSource): TuripSseStreamDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(authRemoteDataSource: AuthRemoteDataSource): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindMemberRemoteDataSource(memberRemoteDataSource: MemberRemoteDataSource): MemberDataSource

    @Binds
    @Singleton
    abstract fun bindGuestRemoteDataSource(guestRemoteDataSource: GuestRemoteDataSource): GuestDataSource

    @Binds
    @Singleton
    abstract fun bindAccountRemoteDataSource(defaultAccountRemoteDataSource: DefaultAccountRemoteDataSource): AccountRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindInstallReferrerDataSource(
        defaultInstallReferrerDataSource: DefaultInstallReferrerDataSource,
    ): InstallReferrerDataSource

    @Binds
    @Singleton
    abstract fun bindDeferredDeepLinkLocalDataSource(
        defaultDeferredDeepLinkLocalDataSource: DefaultDeferredDeepLinkLocalDataSource,
    ): DeferredDeepLinkLocalDataSource
}
