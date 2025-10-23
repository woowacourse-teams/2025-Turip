package com.on.turip.di

import com.on.turip.data.content.place.repository.DefaultContentPlaceRepository
import com.on.turip.data.content.repository.DefaultContentRepository
import com.on.turip.data.favorite.repository.DefaultFavoriteRepository
import com.on.turip.data.folder.repository.DefaultFolderRepository
import com.on.turip.data.place.repository.DefaultFavoritePlaceRepository
import com.on.turip.data.region.repository.DefaultRegionRepository
import com.on.turip.data.searchhistory.repository.DefaultSearchHistoryRepository
import com.on.turip.data.userstorage.repository.DefaultUserStorageRepository
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.folder.repository.FolderRepository
import com.on.turip.domain.region.repository.RegionRepository
import com.on.turip.domain.searchhistory.SearchHistoryRepository
import com.on.turip.domain.trip.repository.ContentPlaceRepository
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
    abstract fun bindContentPlaceRepository(defaultContentPlaceRepository: DefaultContentPlaceRepository): ContentPlaceRepository

    @Binds
    @Singleton
    abstract fun bindRegionRepository(defaultRegionRepository: DefaultRegionRepository): RegionRepository

    @Binds
    @Singleton
    abstract fun bindUserStorageRepository(defaultUserStorageRepository: DefaultUserStorageRepository): UserStorageRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(defaultFavoriteRepository: DefaultFavoriteRepository): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(defaultSearchHistoryRepository: DefaultSearchHistoryRepository): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindFolderRepository(defaultFolderRepository: DefaultFolderRepository): FolderRepository

    @Binds
    @Singleton
    abstract fun bindFavoritePlaceRepository(defaultFavoritePlaceRepository: DefaultFavoritePlaceRepository): FavoritePlaceRepository
}
