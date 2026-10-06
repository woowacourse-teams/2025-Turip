package com.on.turip.feature.popularregion.impl.map

/**
 * 지도에 그릴 한 덩어리의 경계를 어디서 찾을지.
 *
 * 지도는 두 층이다. 아래는 시도 17개([Sido]), 위는 인기 관광지 14곳([Destination]) 이고
 * 두 층의 경계 데이터가 다른 곳에 있어 키로 갈라 둔다.
 *
 * 시도 하나가 통째로 관광지인 6곳(서울·부산·대구·인천·대전·제주)은 두 층의 모양이 같다.
 * 같은 땅을 두 번 그리지 않도록 그런 곳은 [Destination] 하나만 남긴다.
 */
sealed interface RegionShapeKey {
    /** 지도에서 이 덩어리를 가리키는 식별자. 선택 상태를 이 값으로 주고받는다. */
    val code: String

    data class Sido(
        val areaCode: Int,
    ) : RegionShapeKey {
        override val code: String get() = "$SIDO_PREFIX$areaCode"
    }

    data class Destination(
        val regionCategoryName: String,
    ) : RegionShapeKey {
        override val code: String get() = "$DESTINATION_PREFIX$regionCategoryName"
    }

    companion object {
        private const val SIDO_PREFIX: String = "sido:"
        private const val DESTINATION_PREFIX: String = "dest:"
    }
}

/** 이 덩어리의 외곽 링 목록. 그릴 경계가 없으면 빈 목록이다. */
internal fun RegionShapeKey.shapes(): List<List<GeoPoint>> =
    when (this) {
        is RegionShapeKey.Sido -> KoreaRegionShapes.shapesOf(areaCode)
        is RegionShapeKey.Destination -> PopularDestinationShapes.shapesOf(regionCategoryName)
    }
