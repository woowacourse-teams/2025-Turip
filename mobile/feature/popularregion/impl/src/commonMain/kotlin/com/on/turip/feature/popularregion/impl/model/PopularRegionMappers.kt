package com.on.turip.feature.popularregion.impl.model

import com.on.turip.core.model.content.video.VideoInformation
import com.on.turip.core.model.region.RegionVisitor
import com.on.turip.core.ui.util.TuripUrlConverter.convertVideoThumbnailUrl
import com.on.turip.feature.popularregion.impl.map.SidoAreas
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 지도에 놓을 자리를 아는 시도만 화면 모델이 된다.
 *
 * @param regionCategoryNames 이 시도에 속한 튜립 지역 카테고리. 서버가 실제로 갖고 있는 목록에서 추린다.
 */
internal fun RegionVisitor.toUiModel(
    regionCategoryNames: ImmutableList<String> = persistentListOf(),
): PopularRegionModel? {
    val area: SidoAreas.SidoArea = SidoAreas.areaOf(areaCode) ?: return null

    return PopularRegionModel(
        code = areaCode.toString(),
        name = area.shortName,
        location = area.location,
        visitorCount = visitorCount,
        regionCategoryNames = regionCategoryNames,
    )
}

internal fun VideoInformation.toUiModel(): RegionContentModel =
    RegionContentModel(
        contentId = content.id,
        title = content.videoData.title,
        thumbnailUrl = convertVideoThumbnailUrl(content.videoData.url),
        creatorName = content.creator.channelName,
        uploadedDate = content.videoData.uploadedDate,
        nights = trip.tripDuration.nights,
        days = trip.tripDuration.days,
        placeCount = trip.tripPlaceCount,
        isBookmarked = content.isBookmarked,
    )
