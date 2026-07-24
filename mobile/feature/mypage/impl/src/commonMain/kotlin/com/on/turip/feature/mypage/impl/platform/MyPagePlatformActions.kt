package com.on.turip.feature.mypage.impl.platform

import androidx.compose.runtime.Composable
import com.on.turip.feature.mypage.impl.model.InquiryMail

internal class MyPagePlatformActions(
    val openInquiryMail: (InquiryMail) -> Unit,
    val openPrivacyPolicyUrl: (String) -> Unit,
)

@Composable
internal expect fun rememberMyPagePlatformActions(): MyPagePlatformActions

internal fun encodeMailQueryValue(value: String): String =
    buildString {
        value.encodeToByteArray().forEach { byte ->
            val unsigned = byte.toInt() and BYTE_MASK
            val char = unsigned.toChar()
            if (char.isMailQueryAllowed()) {
                append(char)
            } else {
                append('%')
                append(HEX_CHARS[unsigned shr HEX_SHIFT])
                append(HEX_CHARS[unsigned and HEX_MASK])
            }
        }
    }

private fun Char.isMailQueryAllowed(): Boolean =
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
