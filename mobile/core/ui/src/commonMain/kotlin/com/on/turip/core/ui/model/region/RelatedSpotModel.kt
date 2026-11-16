package com.on.turip.core.ui.model.region

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.region.RelatedSpotCategory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 연관 관광지 카드 한 장. 카테고리(관광지/숙박/음식 등) 하나가 카드 하나가 된다.
 *
 * 첫 장소를 대표로 크게 보여주고 나머지는 이어붙여 노출한다.
 */
@Immutable
data class RelatedSpotModel(
    val category: String,
    val spots: ImmutableList<String>,
) {
    val headlineSpot: String = spots.firstOrNull().orEmpty()

    val remainSpots: String = spots.drop(1).joinToString(SPOT_SEPARATOR)

    companion object {
        private const val SPOT_SEPARATOR: String = " · "
    }
}

fun RelatedSpotCategory.toRelatedSpotModel(): RelatedSpotModel =
    RelatedSpotModel(
        category = category,
        spots = spots.toImmutableList(),
    )
