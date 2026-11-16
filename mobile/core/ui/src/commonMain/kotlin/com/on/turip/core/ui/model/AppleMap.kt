package com.on.turip.core.ui.model

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.util.encodeAsUrlComponent

/**
 * Apple 지도(maps.apple.com) 목적지를 나타내는 값 객체.
 * 좌표/장소명은 내부에 감추고, 실제로 열 수 있는 [url]만 노출한다.
 * 좌표가 유효하면 핀 + 라벨을, 없으면 이름 검색으로 대체한다.
 */
@Immutable
data class AppleMap(
    private val placeName: String,
    private val latitude: Double,
    private val longitude: Double,
) {
    val url: String
        get() {
            val encodedName = placeName.encodeAsUrlComponent()
            val hasCoordinates = latitude != 0.0 || longitude != 0.0
            return if (hasCoordinates) {
                "$APPLE_MAP_BASE_URL?ll=$latitude,$longitude&q=$encodedName"
            } else {
                "$APPLE_MAP_BASE_URL?q=$encodedName"
            }
        }

    companion object {
        val Idle = AppleMap("", 0.0, 0.0)
    }
}

private const val APPLE_MAP_BASE_URL = "https://maps.apple.com/"
