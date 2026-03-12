package com.on.turip.ui.compose.invitation

import com.on.turip.ui.compose.invitation.util.InvitationTokenParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InvitationTokenParserTest {
    @Test
    fun `uri token을 우선 추출한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromUrl("https://turip.app/invitations?token=abc123")

        assertThat(actual).isEqualTo("abc123")
    }

    @Test
    fun `url query에 token이 없으면 null을 반환한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromUrl("https://turip.app/invitations?referrer=xyz789")

        assertThat(actual).isNull()
    }

    @Test
    fun `install referrer query에서 referrer token을 추출한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("utm_source=kakao&id=com.on.turip&referrer=def456")

        assertThat(actual).isEqualTo("def456")
    }

    @Test
    fun `install referrer에 referrer가 없으면 null을 반환한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("utm_source=kakao&id=com.on.turip&token=def456")

        assertThat(actual).isNull()
    }
}
