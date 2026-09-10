package com.on.turip.core.ui.util

/**
 * RFC 3986 unreserved 문자(A-Z a-z 0-9 - _ . ~)를 제외한 나머지를 UTF-8 바이트 기준으로
 * percent-encoding 한다. 외부 지도 앱(Apple 지도, 카카오맵 등) 딥링크에 장소명을
 * 쿼리/패스 세그먼트로 그대로 붙일 때 공통으로 사용한다.
 */
fun String.encodeAsUrlComponent(): String =
    buildString {
        this@encodeAsUrlComponent.encodeToByteArray().forEach { byte ->
            val unsigned = byte.toInt() and BYTE_MASK
            val char = unsigned.toChar()
            if (char.isUrlComponentAllowed()) {
                append(char)
            } else {
                append('%')
                append(HEX_CHARS[unsigned shr HEX_SHIFT])
                append(HEX_CHARS[unsigned and HEX_MASK])
            }
        }
    }

private fun Char.isUrlComponentAllowed(): Boolean =
    this in 'A'..'Z' ||
        this in 'a'..'z' ||
        this in '0'..'9' ||
        this == '-' ||
        this == '_' ||
        this == '.' ||
        this == '~'

private const val BYTE_MASK = 0xFF
private const val HEX_MASK = 0x0F
private const val HEX_SHIFT = 4
private const val HEX_CHARS = "0123456789ABCDEF"
