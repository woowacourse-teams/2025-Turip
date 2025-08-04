package com.on.turip.di

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.data.content.dataSource.DefaultContentRemoteDataSource
import com.on.turip.data.creator.dataSource.CreatorRemoteDataSource
import com.on.turip.data.creator.dataSource.DefaultCreatorRemoteDataSource
import com.on.turip.data.favorite.dataSource.DefaultFavoriteRemoteDataSource
import com.on.turip.data.favorite.dataSource.FavoriteRemoteDataSource
import com.on.turip.data.trip.dataSource.DefaultTripRemoteDataSource
import com.on.turip.data.trip.dataSource.TripRemoteDataSource
import com.on.turip.data.userStorage.dataSource.DefaultUserStorageLocalDataSource
import com.on.turip.data.userStorage.dataSource.UserStorageLocalDataSource

object DataSourceModule {
    val contentRemoteDataSource: ContentRemoteDataSource by lazy {
        DefaultContentRemoteDataSource(NetworkModule.contentService)
    }
    val creatorRemoteDataSource: CreatorRemoteDataSource by lazy {
        DefaultCreatorRemoteDataSource(NetworkModule.creatorService)
    }
    val tripRemoteDataSource: TripRemoteDataSource by lazy {
        DefaultTripRemoteDataSource(NetworkModule.tripService)
    }
    val userStorageLocalDataSource: UserStorageLocalDataSource by lazy {
        DefaultUserStorageLocalDataSource(ApplicationContextProvider.applicationContext)
    }
    val favoriteRemoteDataSource: FavoriteRemoteDataSource by lazy {
        DefaultFavoriteRemoteDataSource(NetworkModule.favoriteService)
    }
}
