package com.on.turip.di

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.data.content.dataSource.DefaultContentRemoteDataSource
import com.on.turip.data.creator.dataSource.CreatorRemoteDataSource
import com.on.turip.data.creator.dataSource.DefaultCreatorRemoteDataSource

object DataSourceModule {
    val contentRemoteDataSource: ContentRemoteDataSource by lazy {
        DefaultContentRemoteDataSource(NetworkModule.contentService)
    }

    val creatorRemoteDataSource: CreatorRemoteDataSource by lazy {
        DefaultCreatorRemoteDataSource(NetworkModule.creatorService)
    }
}
