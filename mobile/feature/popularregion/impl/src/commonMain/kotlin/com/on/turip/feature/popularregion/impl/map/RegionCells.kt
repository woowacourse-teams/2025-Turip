package com.on.turip.feature.popularregion.impl.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/**
 * 지역 중심점들로 만든 구획(보로노이 셀).
 *
 * 지도의 탭 판정은 `가장 가까운 중심점`이다. 그 규칙을 그대로 도형으로 바꾸면
 * 각 지역이 차지하는 영역이 나오는데, 그게 곧 보로노이 셀이다.
 * 즉 여기서 나온 경계선은 장식이 아니라 **탭 판정선 그 자체**여서,
 * 화면에 그린 선과 실제로 눌리는 영역이 어긋날 수 없다.
 *
 * 만드는 방법은 반평면 자르기다. 화면 전체 사각형에서 시작해,
 * 다른 모든 중심점과의 수직이등분선으로 계속 잘라내면 남는 볼록다각형이 그 지역의 셀이다.
 * 지역이 수십 개 수준이라 O(n²) 로 충분하고, 화면 크기가 바뀔 때만 다시 계산한다.
 */
internal object RegionCells {
    /**
     * @param sites 지역 중심점의 화면 좌표
     * @return [sites] 와 같은 순서의 셀 목록. 셀은 볼록다각형의 꼭짓점 배열이다.
     */
    fun build(
        sites: List<Offset>,
        size: Size,
    ): List<List<Offset>> {
        if (sites.isEmpty()) return emptyList()

        val canvas: List<Offset> =
            listOf(
                Offset(0f, 0f),
                Offset(size.width, 0f),
                Offset(size.width, size.height),
                Offset(0f, size.height),
            )

        return sites.map { site ->
            var cell: List<Offset> = canvas
            for (other in sites) {
                if (other == site) continue
                cell = cell.clipNearerTo(site = site, other = other)
                if (cell.isEmpty()) break
            }
            cell
        }
    }

    /**
     * [site] 와 [other] 의 수직이등분선으로 다각형을 잘라, [site] 쪽에 가까운 부분만 남긴다.
     * (Sutherland–Hodgman 다각형 클리핑)
     */
    private fun List<Offset>.clipNearerTo(
        site: Offset,
        other: Offset,
    ): List<Offset> {
        if (isEmpty()) return this

        // 수직이등분선: 중점을 지나고 (other - site) 를 법선으로 갖는 직선.
        // signedDistance 가 음수면 site 쪽이다.
        val normal = Offset(other.x - site.x, other.y - site.y)
        val midX: Float = (site.x + other.x) / 2f
        val midY: Float = (site.y + other.y) / 2f

        fun signedDistance(point: Offset): Float =
            ((point.x - midX) * normal.x) + ((point.y - midY) * normal.y)

        val clipped = ArrayList<Offset>(size + 2)
        for (index in indices) {
            val current: Offset = this[index]
            val next: Offset = this[(index + 1) % size]
            val currentDistance: Float = signedDistance(current)
            val nextDistance: Float = signedDistance(next)

            if (currentDistance <= 0f) clipped.add(current)

            // 변이 경계선을 가로지르면 교점을 새 꼭짓점으로 넣는다.
            if ((currentDistance <= 0f) != (nextDistance <= 0f)) {
                val t: Float = currentDistance / (currentDistance - nextDistance)
                clipped.add(
                    Offset(
                        x = current.x + ((next.x - current.x) * t),
                        y = current.y + ((next.y - current.y) * t),
                    ),
                )
            }
        }
        return clipped
    }
}
