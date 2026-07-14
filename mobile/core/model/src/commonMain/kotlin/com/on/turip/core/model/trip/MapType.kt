package com.on.turip.core.model.trip

enum class MapType(
    val baseUrl: String?,
) {
    KAKAO("kakao.com"),
    GOOGLE("google.com"),
    NONE(null),
    ;

    companion object {
        fun from(url: String): MapType {
            val lowercaseUrl = url.lowercase()
            return entries.firstOrNull { mapType -> mapType.baseUrl?.let { lowercaseUrl.contains(it) } == true }
                ?: NONE
        }
    }
}
