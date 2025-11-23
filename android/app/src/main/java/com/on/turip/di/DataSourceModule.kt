package com.on.turip.di

import com.on.turip.data.content.datasource.ContentRemoteDataSource
import com.on.turip.data.content.datasource.DefaultContentRemoteDataSource
import com.on.turip.data.content.place.datasource.ContentPlaceRemoteDataSource
import com.on.turip.data.content.place.datasource.DefaultContentPlaceRemoteDataSource
import com.on.turip.data.favorite.datasource.DefaultFavoriteRemoteDataSource
import com.on.turip.data.favorite.datasource.FavoriteRemoteDataSource
import com.on.turip.data.folder.datasource.DefaultFolderRemoteDataSource
import com.on.turip.data.folder.datasource.FolderRemoteDataSource
import com.on.turip.data.place.datasource.DefaultFavoritePlaceRemoteDataSource
import com.on.turip.data.place.datasource.FavoritePlaceRemoteDataSource
import com.on.turip.data.region.datasource.DefaultRegionRemoteDataSource
import com.on.turip.data.region.datasource.RegionRemoteDataSource
import com.on.turip.data.searchhistory.datasource.DefaultSearchHistoryDataSource
import com.on.turip.data.searchhistory.datasource.SearchHistoryDataSource
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
    abstract fun bindContentPlaceRemoteDataSource(
        defaultContentPlaceRemoteDataSource: DefaultContentPlaceRemoteDataSource,
    ): ContentPlaceRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserStorageLocalDataSource(
        defaultUserStorageLocalDataSource: DefaultUserStorageLocalDataSource,
    ): UserStorageLocalDataSource

    @Binds
    @Singleton
    abstract fun bindFavoriteRemoteDataSource(defaultFavoriteRemoteDataSource: DefaultFavoriteRemoteDataSource): FavoriteRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRegionRemoteDataSource(defaultRegionRemoteDataSource: DefaultRegionRemoteDataSource): RegionRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindSearchHistoryDataSource(defaultSearchHistoryDataSource: DefaultSearchHistoryDataSource): SearchHistoryDataSource

    @Binds
    @Singleton
    abstract fun bindFolderRemoteDataSource(defaultFolderRemoteDataSource: DefaultFolderRemoteDataSource): FolderRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFavoritePlaceRemoteDataSource(
        defaultFavoritePlaceRemoteDataSource: DefaultFavoritePlaceRemoteDataSource,
    ): FavoritePlaceRemoteDataSource
}
