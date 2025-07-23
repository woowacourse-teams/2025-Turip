package com.on.turip.di

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.data.content.dataSource.DefaultContentRemoteDataSource

object DataSourceModule {
    val contentRemoteDataSource: ContentRemoteDataSource by lazy {
        DefaultContentRemoteDataSource(NetworkModule.contentService)
    }
}
