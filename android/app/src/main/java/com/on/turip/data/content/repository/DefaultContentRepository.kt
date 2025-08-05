package com.on.turip.data.content.repository

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.data.content.toDomain
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.PopularFavoriteContent
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.TripDuration

class DefaultContentRepository(
    private val contentRemoteDataSource: ContentRemoteDataSource,
) : ContentRepository {
    override suspend fun loadContentsSize(region: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSize(region)
            .mapCatching { it.count }

    override suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult> =
        contentRemoteDataSource
            .getContents(region, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContent(contentId: Long): Result<VideoData> =
        contentRemoteDataSource
            .getContentDetail(contentId)
            .mapCatching { it.toDomain() }

    override suspend fun loadPopularFavoriteContents(): Result<List<PopularFavoriteContent>> =
        Result.success(
            listOf(
                PopularFavoriteContent(
                    content =
                        Content(
                            id = 1L,
                            creator =
                                Creator(
                                    id = 101L,
                                    channelName = "여행하는 쏭쏭",
                                    profileImage = "https://example.com/images/profile1.jpg",
                                ),
                            videoData =
                                VideoData(
                                    title = "서울의 숨은 명소 5곳",
                                    url = "https://i.ytimg.com/vi/koJ6u7uxEwY/maxresdefault.jpg",
                                    uploadedDate = "2025-06-10",
                                ),
                        ),
                    isFavorite = true,
                    city = "서울",
                    trip =
                        Trip(
                            tripDuration = TripDuration(days = 3, nights = 2),
                            tripPlaceCount = 5,
                        ),
                ),
                PopularFavoriteContent(
                    content =
                        Content(
                            id = 2L,
                            creator =
                                Creator(
                                    id = 102L,
                                    channelName = "맛집탐방러 민지",
                                    profileImage = "https://example.com/images/profile2.jpg",
                                ),
                            videoData =
                                VideoData(
                                    title = "부산 해운대 맛집 투어",
                                    url = "https://i.ytimg.com/vi/koJ6u7uxEwY/maxresdefault.jpg",
                                    uploadedDate = "2025-07-02",
                                ),
                        ),
                    isFavorite = false,
                    city = "부산",
                    trip =
                        Trip(
                            tripDuration = TripDuration(days = 2, nights = 1),
                            tripPlaceCount = 4,
                        ),
                ),
                PopularFavoriteContent(
                    content =
                        Content(
                            id = 3L,
                            creator =
                                Creator(
                                    id = 103L,
                                    channelName = "세계여행자 준호",
                                    profileImage = "https://example.com/images/profile3.jpg",
                                ),
                            videoData =
                                VideoData(
                                    title = "도쿄 여행 브이로그",
                                    url = "https://i.ytimg.com/vi/koJ6u7uxEwY/maxresdefault.jpg",
                                    uploadedDate = "2025-08-01",
                                ),
                        ),
                    isFavorite = true,
                    city = "도쿄",
                    trip =
                        Trip(
                            tripDuration = TripDuration(days = 1, nights = 0),
                            tripPlaceCount = 7,
                        ),
                ),
            ),
        ) // TODO: 추후 API 연결 예정
}
