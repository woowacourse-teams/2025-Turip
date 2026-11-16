package com.on.turip.core.model.trip

enum class MapType(
    val baseUrl: String?,
) {
    KAKAO("kakao.com"),
    GOOGLE("google.com"),
    NONE(null),
    ;

    companion object {
        private const val HTTPS_PREFIX = "https://"

        fun from(url: String): MapType {
            val lowercaseUrl = url.lowercase()
            return entries.firstOrNull { mapType -> mapType.baseUrl?.let { lowercaseUrl.contains(it) } == true }
                ?: NONE
        }

        /**
         * 외부 브라우저/앱으로 열어도 안전한 지도 링크인지 검사한다.
         * https 스킴이면서 host가 허용된 지도 도메인(또는 그 서브도메인)인 경우에만 true.
         */
        fun isAllowedMapUrl(url: String): Boolean {
            val lowercaseUrl = url.trim().lowercase()
            if (!lowercaseUrl.startsWith(HTTPS_PREFIX)) return false
            val host = lowercaseUrl
                .removePrefix(HTTPS_PREFIX)
                .takeWhile { it != '/' && it != '?' && it != '#' }
                .substringAfterLast('@')
                .substringBefore(':')
            if (host.isEmpty()) return false
            return entries.any { mapType ->
                val baseUrl = mapType.baseUrl ?: return@any false
                host == baseUrl || host.endsWith(".$baseUrl")
            }
        }
    }
}
