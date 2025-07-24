package com.on.turip.di

import com.on.turip.data.content.repository.DefaultContentRepository
import com.on.turip.data.content.repository.DefaultCreatorRepository
import com.on.turip.data.content.repository.DefaultTravelCourseRepository
import com.on.turip.domain.videoinfo.contents.creator.repository.CreatorRepository
import com.on.turip.domain.videoinfo.contents.repository.ContentRepository
import com.on.turip.domain.videoinfo.contents.video.trip.repository.TravelCourseRepository

object RepositoryModule {
    val contentRepository: ContentRepository by lazy {
        DefaultContentRepository(DataSourceModule.contentRemoteDataSource)
    }
    val creatorRepository: CreatorRepository by lazy {
        DefaultCreatorRepository()
    }
    val travelCourseRepository: TravelCourseRepository by lazy {
        DefaultTravelCourseRepository()
    }
}
