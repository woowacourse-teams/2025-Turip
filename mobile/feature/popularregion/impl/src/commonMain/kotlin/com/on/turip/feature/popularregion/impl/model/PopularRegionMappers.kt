package com.on.turip.feature.popularregion.impl.model

import com.on.turip.core.model.content.video.VideoInformation
import com.on.turip.core.model.region.DestinationVisitor
import com.on.turip.core.model.region.RegionVisitor
import com.on.turip.core.ui.util.TuripUrlConverter.convertVideoThumbnailUrl
import com.on.turip.feature.popularregion.impl.map.PopularDestinationShapes
import com.on.turip.feature.popularregion.impl.map.RegionShapeKey
import com.on.turip.feature.popularregion.impl.map.SidoAreas

/**
 * 지도에 놓을 자리를 아는 시도만 화면 모델이 된다.
 *
 * 시도 층은 국토를 빈 칸 없이 칠하는 배경이라 콘텐츠도 순위도 붙지 않는다.
 * 그 둘은 위에 덮이는 인기 관광지 층이 갖는다.
 */
internal fun RegionVisitor.toUiModel(): PopularRegionModel? {
    val area: SidoAreas.SidoArea = SidoAreas.areaOf(areaCode) ?: return null

    return PopularRegionModel(
        shapeKey = RegionShapeKey.Sido(areaCode),
        name = area.shortName,
        location = area.location,
        visitorCount = visitorCount,
    )
}

/** 지도에 놓을 자리를 아는 지역 카테고리만 화면 모델이 된다. */
internal fun DestinationVisitor.toUiModel(): PopularRegionModel? {
    val destination: PopularDestinationShapes.Destination =
        PopularDestinationShapes.destinationOf(regionCategoryName) ?: return null

    return PopularRegionModel(
        shapeKey = RegionShapeKey.Destination(regionCategoryName),
        name = regionCategoryName,
        location = destination.location,
        visitorCount = visitorCount,
        rank = rank,
        regionCategoryName = regionCategoryName,
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
