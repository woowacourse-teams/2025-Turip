package com.on.turip.data.content.repository

import com.on.turip.data.content.datasource.ContentRemoteDataSource
import com.on.turip.data.content.toDomain
import com.on.turip.domain.content.City
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.PopularFavoriteContent
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.TripDuration
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DefaultContentRepository(
    private val contentRemoteDataSource: ContentRemoteDataSource,
    private val userStorageRepository: UserStorageRepository,
) : ContentRepository {
    private lateinit var deviceIdentifier: TuripDeviceIdentifier

    init {
        CoroutineScope(Dispatchers.IO).launch {
            userStorageRepository
                .loadId()
                .onSuccess { turipDeviceIdentifier: TuripDeviceIdentifier ->
                    deviceIdentifier = turipDeviceIdentifier
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    override suspend fun loadContentsSizeByRegion(region: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSizeByRegion(region)
            .mapCatching { it.count }

    override suspend fun loadContentsSizeByKeyword(keyword: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSizeByKeyword(keyword)
            .mapCatching { it.count }

    override suspend fun loadContentsByRegion(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult> =
        contentRemoteDataSource
            .getContentsByRegion(region, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult> =
        contentRemoteDataSource
            .getContentsByKeyword(keyword, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContent(contentId: Long): Result<VideoData> =
        contentRemoteDataSource
            .getContentDetail(contentId, deviceIdentifier.fid)
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
                            city = City("서울"),
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
                            city = City("부산"),
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
                            city = City("도쿄"),
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
