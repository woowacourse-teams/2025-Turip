package com.on.turip.feature.popularregion.impl.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation

// 시도 경계 폴리곤을 화면에서 다루기 위한 도구.
// 링 하나는 섬 하나 또는 본토 하나다. 한 지역이 링을 여러 개 가질 수 있고(전남의 다도해),
// 통합시처럼 맞닿은 지역을 합친 경우도 링 여러 개로 들어온다.

/**
 * 링들을 하나의 도형으로 합친 [Path].
 *
 * 링을 각각 서브패스로 넣지 않고 합집합을 내는 이유는 통합시 때문이다.
 * 합친 시도를 서브패스로 두면 사라진 옛 경계가 지역 안쪽에 실선으로 남는다.
 * 전남광주통합시가 그런 경우로, 전남 링이 광주를 감싸고 있어 합집합을 내면 광주 경계가 사라진다.
 * 떨어진 섬끼리는 합집합을 내도 결과가 같아, 두 경우를 한 갈래로 처리한다.
 */
internal fun List<List<GeoPoint>>.toUnionPath(projection: KoreaMapProjection): Path {
    val union = Path()
    forEach { ring ->
        val ringPath: Path = ring.toPath(projection)
        if (union.isEmpty) {
            union.addPath(ringPath)
        } else {
            union.op(union, ringPath, PathOperation.Union)
        }
    }
    return union
}

private fun List<GeoPoint>.toPath(projection: KoreaMapProjection): Path =
    Path().apply {
        forEachIndexed { index, point ->
            val offset: Offset = projection.project(point)
            if (index == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
        }
        close()
    }

/**
 * [point] 가 이 지역 안에 있는지.
 *
 * 링 중 **하나라도** 품고 있으면 안으로 본다. 짝수-홀수 규칙을 쓰면 안 되는데,
 * 경계 데이터에서 구멍을 걷어냈기 때문이다. 전라남도 링은 광주를 통째로 감싸고 있어서
 * 광주 안의 한 점은 두 링 모두에 걸린다. 그 점은 두 지역 다 후보이며,
 * 어느 쪽을 고를지는 넓이로 판단한다 ([geoArea] 가 작은 쪽이 위에 있는 지역이다).
 *
 * 2018년 시도 경계에서 이렇게 감싸이는 관계는 광주-전남 한 쌍뿐이다.
 */
internal fun List<List<GeoPoint>>.containsProjected(
    projection: KoreaMapProjection,
    point: Offset,
): Boolean = any { ring -> ring.ringContainsProjected(projection, point) }

/** 광선 투사(ray casting): 점에서 오른쪽으로 반직선을 쏴 변과 홀수 번 만나면 내부다. */
private fun List<GeoPoint>.ringContainsProjected(
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

/**
 * 위경도 기준 넓이(신발끈 공식). 실제 면적이 아니라 지역끼리 크기를 견주는 용도다.
 *
 * 그리는 순서와 탭 우선순위를 정하는 데 쓴다. 넓은 지역을 먼저 칠해야
 * 그 안에 든 좁은 지역(전남 안의 광주)이 덮이지 않는다.
 */
internal fun List<List<GeoPoint>>.geoArea(): Double = sumOf { ring -> ring.ringArea() }

private fun List<GeoPoint>.ringArea(): Double {
    var doubledArea = 0.0
    for (index in indices) {
        val current: GeoPoint = this[index]
        val next: GeoPoint = this[(index + 1) % size]
        doubledArea += (current.longitude * next.latitude) - (next.longitude * current.latitude)
    }
    return kotlin.math.abs(doubledArea) / 2
}
