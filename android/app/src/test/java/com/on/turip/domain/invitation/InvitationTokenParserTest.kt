package com.on.turip.domain.invitation

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InvitationTokenParserTest {
    @Test
    fun `url이 null이면 null을 반환한다`() {
        val actual = InvitationTokenParser.extractTokenFromUrl(null)

        assertThat(actual).isNull()
    }

    @Test
    fun `url이 blank면 null을 반환한다`() {
        val actual = InvitationTokenParser.extractTokenFromUrl(" ")

        assertThat(actual).isNull()
    }

    @Test
    fun `uri token을 우선 추출한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromUrl("https://turip.app/invitations?token=abc123")

        assertThat(actual).isEqualTo("abc123")
    }

    @Test
    fun `url token 앞뒤 공백은 제거한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromUrl("https://turip.app/invitations?token=%20abc123%20")

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
    fun `install referrer query에서 token을 직접 추출한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("utm_source=kakao&id=com.on.turip&token=def456")

        assertThat(actual).isEqualTo("def456")
    }

    @Test
    fun `install referrer가 token query raw 형태면 token을 추출한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("token=def456")

        assertThat(actual).isEqualTo("def456")
    }

    @Test
    fun `install referrer가 raw token 값만 오면 그대로 추출한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("def456")

        assertThat(actual).isEqualTo("def456")
    }

    @Test
    fun `install referrer raw token 앞뒤 공백은 제거한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("  def456  ")

        assertThat(actual).isEqualTo("def456")
    }

    @Test
    fun `install referrer query에서 인코딩된 referrer token을 추출한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("utm_source=kakao&referrer=token%3Dabc123")

        assertThat(actual).isEqualTo("abc123")
    }

    @Test
    fun `install referrer에 referrer가 없으면 null을 반환한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("utm_source=kakao&id=com.on.turip")

        assertThat(actual).isNull()
    }

    @Test
    fun `install referrer 인코딩이 잘못돼도 null을 반환한다`() {
        val actual =
            InvitationTokenParser.extractTokenFromInstallReferrer("referrer=token%ZZabc")

        assertThat(actual).isNull()
    }
}
