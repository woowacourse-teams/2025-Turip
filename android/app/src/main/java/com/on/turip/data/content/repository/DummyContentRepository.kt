package com.on.turip.data.content.repository

import com.on.turip.domain.contents.Content
import com.on.turip.domain.contents.Creator
import com.on.turip.domain.contents.PagedContentsResult
import com.on.turip.domain.contents.TripDuration
import com.on.turip.domain.contents.VideoInformation
import com.on.turip.domain.contents.repository.ContentRepository

class DummyContentRepository(
    private val dummyData: List<VideoInformation> = dummyVideoInformations,
) : ContentRepository {
    override suspend fun loadContentsSize(region: String): Result<Int> = Result.success(dummyData.size)

    override suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): PagedContentsResult =
        PagedContentsResult(
            videos = dummyData,
            loadable = false,
        )

    companion object {
        val dummyVideoInformations =
            listOf(
                VideoInformation(
                    content =
                        Content(
                            id = 1,
                            creator =
                                Creator(
                                    id = 101,
                                    channelName = "여행하는 뭉치",
                                    profileImage = @Suppress("ktlint:standard:max-line-length")
                                    "https://yt3.ggpht.com/JUGArpQttMzK-jwi4triQrkDf_c_OWv1iikYOimdu63bv_R-bH0U3bY7BOfKs2Gnp2CyP8YBpQ=s88-c-k-c0x00ffffff-no-rj",
                                ),
                            title = "느좋 감성 대구 여행 어쩌구저쩌구",
                            url = "https://youtu.be/dummy1",
                            uploadedDate = "2025-07-15",
                        ),
                    tripDuration =
                        TripDuration(
                            nights = 2,
                            days = 3,
                        ),
                    tripPlaceCount = 5,
                ),
                VideoInformation(
                    content =
                        Content(
                            id = 2,
                            creator =
                                Creator(
                                    id = 102,
                                    channelName = "뚜벅이의 하루",
                                    profileImage = @Suppress("ktlint:standard:max-line-length")
                                    "https://yt3.ggpht.com/UdF_j2R_Prv7kamTRqItI0QUgMvdbJk_xXFjjYGFUIMZUYt1EbrJpEmzJbciKbrrdoX4me8g5w=s176-c-k-c0x00ffffff-no-rj-mo",
                                ),
                            title = "제주도 혼자 여행 브이로그",
                            url = "https://youtu.be/dummy2",
                            uploadedDate = "2025-06-28",
                        ),
                    tripDuration =
                        TripDuration(
                            nights = 3,
                            days = 4,
                        ),
                    tripPlaceCount = 8,
                ),
                VideoInformation(
                    content =
                        Content(
                            id = 3,
                            creator =
                                Creator(
                                    id = 103,
                                    channelName = "감성캠퍼",
                                    profileImage = @Suppress("ktlint:standard:max-line-length")
                                    "https://yt3.ggpht.com/UdF_j2R_Prv7kamTRqItI0QUgMvdbJk_xXFjjYGFUIMZUYt1EbrJpEmzJbciKbrrdoX4me8g5w=s176-c-k-c0x00ffffff-no-rj-mo",
                                ),
                            title = "강원도 캠핑 2박 3일 여행",
                            url = "https://youtu.be/dummy3",
                            uploadedDate = "2025-05-10",
                        ),
                    tripDuration =
                        TripDuration(
                            nights = 2,
                            days = 3,
                        ),
                    tripPlaceCount = 4,
                ),
            )
    }
}
