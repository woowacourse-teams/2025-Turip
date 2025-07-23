package com.on.turip.di

import com.on.turip.data.content.repository.DefaultContentRepository
import com.on.turip.domain.videoinfo.contents.repository.ContentRepository

object RepositoryModule {
    val contentRepository: ContentRepository by lazy {
        DefaultContentRepository(DataSourceModule.contentRemoteDataSource)
    }
}
