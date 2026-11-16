package com.on.turip.core.ui.model.content

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.content.video.VideoInformation
import com.on.turip.core.ui.util.TuripUrlConverter.convertVideoThumbnailUrl

/**
 * 목록에 한 줄로 놓이는 영상 요약.
 *
 * 랜덤 여행 브리핑과 지역 브리핑이 같은 카드를 쓰기 때문에 특정 feature 에 두지 않는다.
 */
@Immutable
data class VideoSummaryModel(
    val contentId: Long,
    val title: String,
    val thumbnailUrl: String,
    val channelName: String,
    val profileImageUrl: String,
    val uploadedDate: String,
    val cityName: String,
    val nights: Int,
    val days: Int,
    val placeCount: Int,
)

fun VideoInformation.toVideoSummaryModel(): VideoSummaryModel =
    VideoSummaryModel(
        contentId = content.id,
        title = content.videoData.title,
        thumbnailUrl = convertVideoThumbnailUrl(content.videoData.url),
        channelName = content.creator.channelName,
        profileImageUrl = content.creator.profileImage,
        uploadedDate = content.videoData.uploadedDate,
        cityName = content.city.name,
        nights = trip.tripDuration.nights,
        days = trip.tripDuration.days,
        placeCount = trip.tripPlaceCount,
    )
