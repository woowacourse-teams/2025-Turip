package com.on.turip.core.ui.model

import androidx.compose.runtime.Immutable

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
            val encodedName = placeName.encodeUrlQueryComponent()
            val hasCoordinates = latitude != 0.0 || longitude != 0.0
            return if (hasCoordinates) {
                "$APPLE_MAP_BASE_URL?ll=$latitude,$longitude&q=$encodedName"
            } else {
                "$APPLE_MAP_BASE_URL?q=$encodedName"
            }
        }

    companion object{
        val Idle = AppleMap("", 0.0, 0.0)
    }
}

private fun String.encodeUrlQueryComponent(): String =
    buildString {
        this@encodeUrlQueryComponent.encodeToByteArray().forEach { byte ->
            val unsigned = byte.toInt() and BYTE_MASK
            val char = unsigned.toChar()
            if (char.isUrlQueryAllowed()) {
                append(char)
            } else {
                append('%')
                append(HEX_CHARS[unsigned shr HEX_SHIFT])
                append(HEX_CHARS[unsigned and HEX_MASK])
            }
        }
    }

private fun Char.isUrlQueryAllowed(): Boolean =
    this in 'A'..'Z' ||
        this in 'a'..'z' ||
        this in '0'..'9' ||
        this == '-' ||
        this == '_' ||
        this == '.' ||
        this == '~'

private const val APPLE_MAP_BASE_URL = "https://maps.apple.com/"
private const val BYTE_MASK = 0xFF
private const val HEX_MASK = 0x0F
private const val HEX_SHIFT = 4
private const val HEX_CHARS = "0123456789ABCDEF"
