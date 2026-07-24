package com.on.turip.core.common.deeplink

import com.on.turip.core.model.invitation.InvitationTokenParser

/**
 * 딥링크 URL을 안전하게 파싱한다. **어떤 입력에도 예외를 던지지 않는 total function.**
 *
 * Swift 진입점에서 호출되는 [DeepLinkStore.offer] 내부에서 사용되므로, malformed URL이어도
 * 안정적으로 [DeepLinkParseResult]를 반환해야 한다.
 */
fun parseDeepLinkSafely(url: String?): DeepLinkParseResult {
    val token: String? =
        runCatching { InvitationTokenParser.extractTokenFromUrl(url) }
            .getOrNull()
            ?.trim()
            ?.takeIf(String::isNotEmpty)

    return when {
        token != null -> DeepLinkParseResult.Supported(ActionableDeepLinkRoute.Invitation(token))
        url.isNullOrBlank() -> DeepLinkParseResult.Unsupported(UnsupportedReason.MALFORMED)
        else -> DeepLinkParseResult.Unsupported(UnsupportedReason.MISSING_TOKEN)
    }
}
