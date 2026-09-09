package com.on.turip.feature.popularregion.impl.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min

/**
 * 위경도를 캔버스 좌표로 옮기는 등장방형(equirectangular) 투영.
 *
 * 지도 위 모든 위치를 실제 위경도로 두고 그릴 때만 화면 좌표로 바꾼다.
 * 지역을 추가할 때 눈대중으로 화면 좌표를 잡을 필요가 없어진다.
 *
 * 경도 1도의 실제 거리는 위도가 올라갈수록 짧아지므로 [LONGITUDE_SCALE] 로 보정한다.
 * 보정하지 않으면 한반도가 가로로 퍼져 보인다.
 */
internal class KoreaMapProjection private constructor(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
) {
    /**
     * 이 투영으로 찍은 좌표를 [target] 투영의 좌표로 옮기는 배율과 이동량.
     *
     * 두 투영은 배율과 평행이동만 다르므로, 폴리곤을 매번 다시 투영하지 않고
     * 이미 만들어 둔 [androidx.compose.ui.graphics.Path] 를 변환해 쓸 수 있다.
     * 바텀시트가 열리고 닫히는 동안 지도가 매 프레임 새로 만들어지는 것을 막는다.
     *
     * 원점 기준으로 [Transform.scale] 배 키운 뒤 [Transform.translateX] · [Transform.translateY] 만큼 옮기면 된다.
     */
    fun transformTo(target: KoreaMapProjection): Transform {
        val ratio: Float = target.scale / scale
        return Transform(
            scale = ratio,
            translateX = target.offsetX - (ratio * offsetX),
            translateY = target.offsetY - (ratio * offsetY),
        )
    }

    data class Transform(
        val scale: Float,
        val translateX: Float,
        val translateY: Float,
    )

    fun project(point: GeoPoint): Offset = project(point.latitude, point.longitude)

    fun project(
        latitude: Double,
        longitude: Double,
    ): Offset =
        Offset(
            x = offsetX + (((longitude - MIN_LONGITUDE) * LONGITUDE_SCALE).toFloat() * scale),
            y = offsetY + ((MAX_LATITUDE - latitude).toFloat() * scale),
        )

    companion object {
        /** 남한 전역 + 제주를 담는 경계 상자 */
        private const val MIN_LATITUDE: Double = 33.00
        private const val MAX_LATITUDE: Double = 38.70
        private const val MIN_LONGITUDE: Double = 125.55
        private const val MAX_LONGITUDE: Double = 129.80

        private const val MEAN_LATITUDE: Double = (MIN_LATITUDE + MAX_LATITUDE) / 2
        private val LONGITUDE_SCALE: Double = cos(MEAN_LATITUDE * PI / 180)

        private const val SPAN_LATITUDE: Double = MAX_LATITUDE - MIN_LATITUDE
        private val SPAN_LONGITUDE: Double = (MAX_LONGITUDE - MIN_LONGITUDE) * LONGITUDE_SCALE

        /**
         * [size] 안에 종횡비를 유지한 채 지도를 꽉 채워 배치하는 투영을 만든다.
         *
         * @param bottomInset 아래쪽에서 바텀시트가 덮는 높이(px).
         * 캔버스는 항상 화면 전체지만, 지도는 시트에 가리지 않는 위쪽 영역에만 맞춘다.
         * 시트가 없을 때 0 을 넘기면 지도가 화면을 꽉 채운다.
         * @param verticalBias 0f 면 위쪽, 0.5f 면 가운데, 1f 면 아래쪽 정렬.
         */
        fun fit(
            size: Size,
            bottomInset: Float = 0f,
            verticalBias: Float = DEFAULT_VERTICAL_BIAS,
        ): KoreaMapProjection {
            val usableHeight: Float =
                (size.height - bottomInset).coerceAtLeast(MIN_USABLE_HEIGHT)
            val scale: Float =
                min(
                    size.width / SPAN_LONGITUDE.toFloat(),
                    usableHeight / SPAN_LATITUDE.toFloat(),
                )
            val mapWidth: Float = SPAN_LONGITUDE.toFloat() * scale
            val mapHeight: Float = SPAN_LATITUDE.toFloat() * scale
            return KoreaMapProjection(
                scale = scale,
                offsetX = (size.width - mapWidth) / 2f,
                offsetY = (usableHeight - mapHeight) * verticalBias,
            )
        }

        private const val DEFAULT_VERTICAL_BIAS: Float = 0.5f
        private const val MIN_USABLE_HEIGHT: Float = 1f
    }
}
