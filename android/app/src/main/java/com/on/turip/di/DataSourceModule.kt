package com.on.turip.di

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.data.content.dataSource.DefaultContentRemoteDataSource
import com.on.turip.data.creator.dataSource.CreatorRemoteDataSource
import com.on.turip.data.creator.dataSource.DefaultCreatorRemoteDataSource
import com.on.turip.data.trip.dataSource.DefaultTripRemoteDataSource
import com.on.turip.data.trip.dataSource.TripRemoteDataSource

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
}
