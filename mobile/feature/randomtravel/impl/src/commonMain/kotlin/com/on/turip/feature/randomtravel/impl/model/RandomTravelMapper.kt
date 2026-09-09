package com.on.turip.feature.randomtravel.impl.model

import com.on.turip.core.model.content.video.VideoInformation
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.region.RelatedSpotCategory
import com.on.turip.core.model.trip.ContentPlace
import com.on.turip.core.ui.util.TuripUrlConverter.convertVideoThumbnailUrl
import kotlinx.collections.immutable.toImmutableList

internal fun VideoInformation.toUiModel(): RandomTravelVideoModel =
    RandomTravelVideoModel(
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

/**
 * 국내/해외는 [RegionCategory] 자체로는 알 수 없다.
 * 후보 풀을 조회할 때 사용한 조건(`loadRegionCategories(isDomestic)`)을 그대로 전달받는다.
 */
internal fun RegionCategory.toUiModel(
    isDomestic: Boolean,
    videoCount: Int,
): RandomDestinationModel =
    RandomDestinationModel(
        name = name,
        imageUrl = imageUrl,
        isDomestic = isDomestic,
        videoCount = videoCount,
    )

internal fun RelatedSpotCategory.toUiModel(): RandomTravelRelatedSpotModel =
    RandomTravelRelatedSpotModel(
        category = category,
        spots = spots.toImmutableList(),
    )

/**
 * 장소 카테고리는 `음식점 > 한식 > 육류,고기` 처럼 대분류부터 내려오는 문자열이라
 * 시트에서는 가장 마지막 분류만 보여준다.
 */
internal fun ContentPlace.toTuripDraftUiModel(): TuripDraftPlaceModel =
    TuripDraftPlaceModel(
        placeId = place.placeId,
        name = place.name,
        category = place.category.lastOrNull().orEmpty(),
    )
