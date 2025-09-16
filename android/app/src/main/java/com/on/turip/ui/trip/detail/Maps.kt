package com.on.turip.ui.trip.detail

import android.net.Uri
import androidx.annotation.DrawableRes
import com.on.turip.R
import com.on.turip.ui.trip.detail.Maps.entries

private const val KAKAO_BASE_URL = "kakao.com"
private const val GOOGLE_BASE_URL = "google.com"

enum class Maps(
    val url: String,
    @DrawableRes val iconRes: Int,
) {
    KaKao(KAKAO_BASE_URL, R.drawable.ic_kakao_map_basic),
    Google(GOOGLE_BASE_URL, R.drawable.ic_google_map_basic),
    ;

    companion object {
        private const val NOT_FOUND = "%s not found"

        fun contains(mapLink: Uri): Maps =
            entries.find { mapLink.toString().contains(it.url) }
                ?: throw IllegalArgumentException(NOT_FOUND.format(mapLink))
    }
}
