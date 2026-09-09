package com.on.turip.feature.popularregion.impl.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

/**
 * 남한 해안선을 단순화한 폴리곤.
 *
 * 시군구 229개 폴리곤을 손으로 만들면 인접 경계가 서로 어긋나 지도에 틈이 생긴다.
 * 그래서 행정경계는 그리지 않고 바깥 윤곽선 하나만 둔다.
 * 지역별 농담은 [com.on.turip.feature.popularregion.impl.component.KoreaHeatMap] 이
 * 이 윤곽 안쪽에 방사형 그라데이션으로 칠한다.
 *
 * 좌표는 서해안을 따라 남하 → 남해안 동진 → 동해안 북상 → 휴전선 서진 순서다.
 * 울릉도·독도는 [KoreaMapProjection] 의 경계 상자 밖이라 제외했다.
 */
internal object KoreaOutline {
    val MAINLAND: List<GeoPoint> =
        listOf(
            // 서해안 (한강 하구 → 땅끝)
            GeoPoint(37.78, 126.68),
            GeoPoint(37.70, 126.42),
            GeoPoint(37.58, 126.35),
            GeoPoint(37.44, 126.55),
            GeoPoint(37.32, 126.62),
            GeoPoint(37.10, 126.55),
            GeoPoint(36.98, 126.75),
            GeoPoint(36.90, 126.35),
            GeoPoint(36.78, 126.13),
            GeoPoint(36.62, 126.25),
            GeoPoint(36.50, 126.40),
            GeoPoint(36.32, 126.50),
            GeoPoint(36.18, 126.48),
            GeoPoint(36.00, 126.60),
            GeoPoint(35.85, 126.60),
            GeoPoint(35.72, 126.42),
            GeoPoint(35.60, 126.55),
            GeoPoint(35.42, 126.42),
            GeoPoint(35.20, 126.35),
            GeoPoint(35.02, 126.30),
            GeoPoint(34.90, 126.32),
            GeoPoint(34.75, 126.22),
            GeoPoint(34.60, 126.30),
            GeoPoint(34.42, 126.35),
            GeoPoint(34.30, 126.52),
            // 남해안 (땅끝 → 부산)
            GeoPoint(34.38, 126.72),
            GeoPoint(34.50, 126.90),
            GeoPoint(34.62, 127.05),
            GeoPoint(34.55, 127.25),
            GeoPoint(34.60, 127.48),
            GeoPoint(34.75, 127.55),
            GeoPoint(34.72, 127.75),
            GeoPoint(34.88, 127.85),
            GeoPoint(34.92, 128.05),
            GeoPoint(34.85, 128.15),
            GeoPoint(34.98, 128.28),
            GeoPoint(34.83, 128.42),
            GeoPoint(34.75, 128.68),
            GeoPoint(35.08, 128.75),
            GeoPoint(35.08, 129.02),
            GeoPoint(35.16, 129.22),
            // 동해안 (부산 → 고성)
            GeoPoint(35.48, 129.42),
            GeoPoint(35.72, 129.48),
            GeoPoint(36.08, 129.57),
            GeoPoint(36.10, 129.40),
            GeoPoint(36.42, 129.45),
            GeoPoint(36.72, 129.47),
            GeoPoint(37.05, 129.42),
            GeoPoint(37.30, 129.30),
            GeoPoint(37.45, 129.17),
            GeoPoint(37.55, 129.10),
            GeoPoint(37.78, 128.95),
            GeoPoint(38.00, 128.75),
            GeoPoint(38.20, 128.60),
            GeoPoint(38.38, 128.45),
            // 휴전선 (고성 → 한강 하구)
            GeoPoint(38.32, 128.30),
            GeoPoint(38.30, 128.05),
            GeoPoint(38.20, 127.80),
            GeoPoint(38.14, 127.50),
            GeoPoint(38.30, 127.22),
            GeoPoint(38.20, 127.00),
            GeoPoint(38.02, 126.90),
            GeoPoint(37.90, 126.78),
        )

    val JEJU: List<GeoPoint> =
        listOf(
            GeoPoint(33.38, 126.97),
            GeoPoint(33.46, 126.91),
            GeoPoint(33.52, 126.76),
            GeoPoint(33.54, 126.55),
            GeoPoint(33.52, 126.34),
            GeoPoint(33.46, 126.19),
            GeoPoint(33.38, 126.13),
            GeoPoint(33.30, 126.19),
            GeoPoint(33.24, 126.34),
            GeoPoint(33.22, 126.55),
            GeoPoint(33.24, 126.76),
            GeoPoint(33.30, 126.91),
        )

    private val ISLANDS: List<List<GeoPoint>> = listOf(MAINLAND, JEJU)

    /**
     * [point] 가 육지 위인지.
     *
     * 탭이 육지면 어느 지역이든 반드시 하나가 선택되고, 바다면 시트를 닫는다.
     * 화면에 그린 구획선과 실제 눌리는 영역이 어긋나지 않도록, 거리 임계값 대신 이 판정을 쓴다.
     */
    fun contains(
        projection: KoreaMapProjection,
        point: Offset,
    ): Boolean = ISLANDS.any { island -> island.containsProjected(projection, point) }

    /** 광선 투사(ray casting): 점에서 오른쪽으로 반직선을 쏴 변과 홀수 번 만나면 내부다. */
    private fun List<GeoPoint>.containsProjected(
        projection: KoreaMapProjection,
        point: Offset,
    ): Boolean {
        var isInside = false
        var previousIndex: Int = lastIndex
        for (index in indices) {
            val current: Offset = projection.project(this[index])
            val previous: Offset = projection.project(this[previousIndex])
            val crossesRay: Boolean = (current.y > point.y) != (previous.y > point.y)
            if (crossesRay) {
                val intersectionX: Float =
                    (
                        ((previous.x - current.x) * (point.y - current.y)) /
                            (previous.y - current.y)
                    ) + current.x
                if (point.x < intersectionX) isInside = !isInside
            }
            previousIndex = index
        }
        return isInside
    }

    /** 본토와 제주를 한 [Path] 로 묶는다. 열 블롭을 이 Path 로 잘라내 바다로 번지지 않게 한다. */
    fun buildPath(projection: KoreaMapProjection): Path =
        Path().apply {
            ISLANDS.forEach { island ->
                island.forEachIndexed { index, point ->
                    val offset = projection.project(point)
                    if (index == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                }
                close()
            }
        }
}
