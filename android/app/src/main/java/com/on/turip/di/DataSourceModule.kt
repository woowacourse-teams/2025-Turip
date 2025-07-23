package com.on.turip.di

import com.on.turip.data.contents.dataSource.ContentsRemoteDataSource
import com.on.turip.data.contents.dataSource.DefaultContentsRemoteDataSource

object DataSourceModule {
    val contentsRemoteDataSource: ContentsRemoteDataSource by lazy {
        DefaultContentsRemoteDataSource(NetworkModule.contentsService)
    }
}
