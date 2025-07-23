package com.on.turip.di

import com.on.turip.data.contents.repository.DefaultContentsRepository
import com.on.turip.domain.contents.repository.ContentsRepository

object RepositoryModule {
    val contentsRepository: ContentsRepository by lazy {
        DefaultContentsRepository(DataSourceModule.contentsRemoteDataSource)
    }
}
