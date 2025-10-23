package com.on.turip.di

import com.on.turip.data.content.place.service.ContentPlaceService
import com.on.turip.data.content.service.ContentService
import com.on.turip.data.favorite.service.FavoriteService
import com.on.turip.data.folder.service.FolderService
import com.on.turip.data.place.service.PlaceService
import com.on.turip.data.region.service.RegionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideContentService(retrofit: Retrofit): ContentService = retrofit.create<ContentService>()

    @Provides
    @Singleton
    fun provideFavoriteService(retrofit: Retrofit): FavoriteService = retrofit.create<FavoriteService>()

    @Provides
    @Singleton
    fun provideFolderService(retrofit: Retrofit): FolderService = retrofit.create<FolderService>()

    @Provides
    @Singleton
    fun providePlaceService(retrofit: Retrofit): PlaceService = retrofit.create<PlaceService>()

    @Provides
    @Singleton
    fun provideRegionService(retrofit: Retrofit): RegionService = retrofit.create<RegionService>()

    @Provides
    @Singleton
    fun provideContentPlaceService(retrofit: Retrofit): ContentPlaceService = retrofit.create<ContentPlaceService>()
}
