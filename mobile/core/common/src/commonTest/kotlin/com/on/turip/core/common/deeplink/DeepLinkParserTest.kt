package com.on.turip.core.common.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DeepLinkParserTest {
    @Test
    fun `token이 있는 초대 URL은 Supported Invitation으로 파싱된다`() {
        val result = parseDeepLinkSafely("https://invite.turip.kro.kr/invitations/?token=abc123")

        val supported = assertIs<DeepLinkParseResult.Supported>(result)
        assertEquals(ActionableDeepLinkRoute.Invitation("abc123"), supported.route)
    }

    @Test
    fun `token이 없는 URL은 Unsupported MISSING_TOKEN으로 파싱된다`() {
        val result = parseDeepLinkSafely("https://invite.turip.kro.kr/invitations/")

        val unsupported = assertIs<DeepLinkParseResult.Unsupported>(result)
        assertEquals(UnsupportedReason.MISSING_TOKEN, unsupported.reason)
    }

    @Test
    fun `null이나 빈 문자열은 Unsupported MALFORMED로 파싱된다`() {
        assertEquals(
            UnsupportedReason.MALFORMED,
            (parseDeepLinkSafely(null) as DeepLinkParseResult.Unsupported).reason,
        )
        assertEquals(
            UnsupportedReason.MALFORMED,
            (parseDeepLinkSafely("   ") as DeepLinkParseResult.Unsupported).reason,
        )
    }

    @Test
    fun `dedupeKey와 toString에 raw token이 노출되지 않는다`() {
        val token = "super-secret-token"
        val route = ActionableDeepLinkRoute.Invitation(token)

        assertFalse(route.dedupeKey.contains(token), "dedupeKey에 원본 토큰이 있으면 안 된다")
        assertFalse(route.toString().contains(token), "toString에 원본 토큰이 있으면 안 된다")
        assertTrue(route.dedupeKey.startsWith("invitation:"))
    }

    @Test
    fun `sha256Fingerprint는 결정적이고 64자 hex를 반환한다`() {
        val a = sha256Fingerprint("hello")
        val b = sha256Fingerprint("hello")

        assertEquals(a, b)
        assertEquals(64, a.length)
        assertTrue(a.all { it in "0123456789abcdef" })
        // 알려진 SHA-256("hello") 벡터
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", a)
    }
}
