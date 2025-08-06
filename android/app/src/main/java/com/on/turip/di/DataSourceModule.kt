package com.on.turip.di

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.data.content.dataSource.DefaultContentRemoteDataSource
import com.on.turip.data.creator.dataSource.CreatorRemoteDataSource
import com.on.turip.data.creator.dataSource.DefaultCreatorRemoteDataSource
import com.on.turip.data.region.datasource.DefaultRegionRemoteDataSource
import com.on.turip.data.region.datasource.RegionRemoteDataSource
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
    val regionRemoteDataSource: RegionRemoteDataSource by lazy {
        DefaultRegionRemoteDataSource(NetworkModule.regionService)
    }
}
