package com.on.turip.domain.invitation

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * 초대 링크 또는 InstallReferrer에서 초대 토큰을 안전하게 추출한다.
 */
object InvitationTokenParser {
    private const val INVITATION_TOKEN_QUERY = "token"
    private const val INVITATION_REFERRER_QUERY = "referrer"

    fun extractTokenFromUrl(url: String?): String? {
        val query = extractQueryFromUrl(url) ?: return null
        return extractQueryParameter(query, INVITATION_TOKEN_QUERY)
            ?.trim()
            ?.takeIf(String::isNotEmpty)
    }

    fun extractTokenFromInstallReferrer(installReferrer: String?): String? {
        if (installReferrer.isNullOrBlank()) return null

        val queryOrRaw: String = extractQueryOrRaw(installReferrer)
        if (!queryOrRaw.contains('=')) {
            return queryOrRaw.trim().takeIf(String::isNotEmpty)
        }

        val tokenFromDirectTokenQuery: String? = extractQueryParameter(queryOrRaw, INVITATION_TOKEN_QUERY)
        if (!tokenFromDirectTokenQuery.isNullOrBlank()) {
            return tokenFromDirectTokenQuery
                .trim()
                .takeIf(String::isNotEmpty)
        }

        val referrerValue: String = extractQueryParameter(queryOrRaw, INVITATION_REFERRER_QUERY) ?: return null
        val tokenFromReferrerQuery: String? = extractQueryParameter(extractQueryOrRaw(referrerValue), INVITATION_TOKEN_QUERY)
        return tokenFromReferrerQuery
            ?.trim()
            ?.takeIf(String::isNotEmpty)
            ?: referrerValue
                .trim()
                .takeIf(String::isNotEmpty)
    }

    private fun extractQueryFromUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val query = url.substringAfter('?', missingDelimiterValue = "")
        return query.takeIf(String::isNotBlank)
    }

    private fun extractQueryOrRaw(value: String): String = value.substringAfter('?', missingDelimiterValue = value)

    private fun extractQueryParameter(
        query: String,
        key: String,
    ): String? =
        query
            .split('&')
            .asSequence()
            .mapNotNull { parameter: String ->
                val delimiterIndex = parameter.indexOf('=')
                if (delimiterIndex < 0) return@mapNotNull null

                val name = decodeSafely(parameter.substring(0, delimiterIndex)) ?: return@mapNotNull null
                if (name != key) return@mapNotNull null

                decodeSafely(parameter.substring(delimiterIndex + 1))
            }.firstOrNull(String::isNotBlank)

    /**
     * `URLDecoder.decode(String, Charset)`은 Android API 레벨별 호환 이슈가 있어
     * API 1부터 지원되는 `decode(String, String)` 시그니처를 사용한다.
     */
    private fun decodeSafely(value: String): String? =
        runCatching {
            URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        }.getOrNull()
}
