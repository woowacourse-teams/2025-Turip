package com.on.turip.feature.popularregion.impl.map

// 경계 폴리곤을 소스에 넣어 두기 위한 문자열 형식과 그 해석기.
// 시도([KoreaRegionShapes])와 인기 관광지([PopularDestinationShapes])가 같은 형식을 쓴다.

/** 좌표 정수의 실제 단위. 1/10000도 ≈ 11m */
private const val COORDINATE_SCALE: Double = 10_000.0

private const val RING_SEPARATOR: Char = '|'
private const val POINT_SEPARATOR: Char = ' '
private const val VALUE_SEPARATOR: Char = ','

/**
 * 델타로 적힌 좌표를 위경도로 되돌린다.
 *
 * 링은 `|`, 점은 공백, 위도와 경도는 `,` 로 나눈다.
 * 값은 1/10000도 단위 정수이고 **직전 점과의 차이**만 적는다. 첫 점은 0 에서 시작하므로 절댓값이 된다.
 *
 * 누적합이라 한 점이라도 건너뛰면 이후가 전부 어긋난다. 손으로 고칠 때 주의할 부분이다.
 */
internal fun String.decodeRings(): List<List<GeoPoint>> =
    split(RING_SEPARATOR).map { ring ->
        var latitude = 0
        var longitude = 0
        ring.split(POINT_SEPARATOR).map { point ->
            val separatorIndex: Int = point.indexOf(VALUE_SEPARATOR)
            latitude += point.substring(0, separatorIndex).toInt()
            longitude += point.substring(separatorIndex + 1).toInt()
            GeoPoint(
                latitude = latitude / COORDINATE_SCALE,
                longitude = longitude / COORDINATE_SCALE,
            )
        }
    }
